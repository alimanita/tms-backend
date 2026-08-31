package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.PleinCarburant;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.PleinCarburantMapper;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.PleinCarburantRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.FileStorageService;
import com.transport.tms.service.fleet.PleinCarburantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PleinCarburantServiceImpl implements PleinCarburantService {

    private final PleinCarburantRepository pleinRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final PleinCarburantMapper mapper;
    private final FileStorageService fileStorageService;
    private final UtilisateurRepository utilisateurRepository;
    private final com.transport.tms.service.fleet.ReceiptOcrService receiptOcrService;


    @Override
    public PleinCarburantResponse create(PleinCarburantRequest request, MultipartFile proof) {
        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Véhicule introuvable avec l'ID = " + request.vehiculeId()));
        log.info("=== createPleinCarburant ===");
        log.info("Request: vehiculeId={}, fuelType={}, quantity={}",
                request.vehiculeId(), request.fuelType(), request.quantityLiters());
        if (request.mileageAfter() != null) {
            BigDecimal dernierKm = vehicule.getKilometrageActuel();
            if (request.mileageAfter().compareTo(dernierKm) < 0) {
                throw new InvalidOperationException(
                        "Le kilométrage du plein (" + request.mileageAfter()
                                + ") ne peut pas être inférieur au kilométrage actuel ("
                                + dernierKm + ")");
            }
        }

        PleinCarburant plein = mapper.toEntity(request);
        plein.setReference(generateReference());
        plein.setVehicule(vehicule);

        // ── Résolution du chauffeur ──────────────────────────────────
        Chauffeur chauffeur;
        if (request.chauffeurId() != null) {
            // Chauffeur explicitement fourni (ex: saisie par un admin/gestionnaire)
            chauffeur = chauffeurRepository.findById(request.chauffeurId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Chauffeur introuvable avec l'ID = " + request.chauffeurId()));
        } else {
            // Aucun chauffeur fourni : on déduit de l'utilisateur connecté (si c'est un chauffeur)
            chauffeur = resolveChauffeurFromConnectedUser();
        }
        plein.setChauffeur(chauffeur);

        if (proof != null && !proof.isEmpty()) {
            plein.setProofFilePath(fileStorageService.store(proof));
        }

        if (request.mileageAfter() != null) {
            vehicule.setKilometrageActuel(request.mileageAfter());
            vehiculeRepository.save(vehicule);
        }

        return mapper.toResponse(pleinRepository.save(plein));
    }

    @Override
    public PleinCarburantResponse update(Long id, PleinCarburantRequest request, MultipartFile proof) {
        PleinCarburant plein = findEntityById(id);

        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Véhicule introuvable avec l'ID = " + request.vehiculeId()));

        // Mise à jour des champs métiers
        plein.setFillingDate(request.fillingDate());
        plein.setFuelType(request.fuelType());
        plein.setQuantityLiters(request.quantityLiters());
        plein.setPricePerLiter(request.pricePerLiter());
        plein.setMileageBefore(request.mileageBefore());
        plein.setMileageAfter(request.mileageAfter());
        plein.setIsFullTank(request.isFullTank() != null ? request.isFullTank() : true);
        plein.setReceiptNumber(request.receiptNumber());
        plein.setNotes(request.notes());
        plein.setAmountHT(request.amountHT());
        plein.setAmountTTC(request.amountTTC());
        plein.setTvaRate(request.tvaRate());
        plein.setTvaAmount(request.tvaAmount());
        plein.setIsTvaRecoverable(request.isTvaRecoverable() != null ? request.isTvaRecoverable() : false);
        plein.setRecoverableTvaAmount(request.recoverableTvaAmount());
        plein.setAcciseAmount(request.acciseAmount());
        plein.setVehicule(vehicule);
        plein.calculerConsommation();

        // Chauffeur
        if (request.chauffeurId() != null) {
            Chauffeur chauffeur = chauffeurRepository.findById(request.chauffeurId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Chauffeur introuvable avec l'ID = " + request.chauffeurId()));
            plein.setChauffeur(chauffeur);
        }

        // Nouveau fichier justificatif fourni → on remplace
        if (proof != null && !proof.isEmpty()) {
            plein.setProofFilePath(fileStorageService.store(proof));
        }

        // Mise à jour kilométrage véhicule si nécessaire
        if (request.mileageAfter() != null) {
            vehicule.setKilometrageActuel(request.mileageAfter());
            vehiculeRepository.save(vehicule);
        }

        return mapper.toResponse(pleinRepository.save(plein));
    }

    /**
     * Déduit le chauffeur à partir de l'utilisateur authentifié,
     * uniquement si celui-ci a le rôle CHAUFFEUR (évite qu'un admin/gestionnaire
     * se voie attribuer par erreur un plein qu'il saisit pour un tiers).
     */
    private Chauffeur resolveChauffeurFromConnectedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.warn("Aucun utilisateur authentifié : impossible de déduire le chauffeur du plein");
            return null;
        }

        boolean isChauffeurRole = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CHAUFFEUR"));
        if (!isChauffeurRole) {
            log.debug("Utilisateur '{}' n'a pas le rôle CHAUFFEUR : chauffeurId non déduit", auth.getName());
            return null;
        }

        String username = auth.getName();
        Utilisateur utilisateur = utilisateurRepository.findByEmailOrUsername(username, username)
                .orElse(null);

        if (utilisateur == null) {
            log.warn("Aucun utilisateur trouvé en base pour le login connecté '{}'", username);
            return null;
        }

        Chauffeur chauffeur = chauffeurRepository.findByUtilisateurId(utilisateur.getId())
                .orElse(null);

        if (chauffeur == null) {
            log.warn("Aucun chauffeur lié à l'utilisateur '{}' (id={})", username, utilisateur.getId());
        }

        return chauffeur;
    }

    @Override
    @Transactional(readOnly = true)
    public PleinCarburantResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PleinCarburantResponse> findAll(Pageable pageable) {
        Chauffeur chauffeur = resolveChauffeurFromConnectedUser();
        if (chauffeur != null) {
            // ROLE_CHAUFFEUR → uniquement ses propres pleins
            return pleinRepository.findByChauffeurId(chauffeur.getId(), pageable)
                    .map(mapper::toResponse);
        }
        // Admin / Gestionnaire → tous les pleins
        return pleinRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PleinCarburantResponse> findByVehicule(Long vehiculeId) {
        return pleinRepository.findByVehiculeIdOrderByFillingDateDesc(vehiculeId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PleinCarburantResponse> findByChauffeur(Long chauffeurId) {
        return pleinRepository.findByChauffeurIdOrderByFillingDateDesc(chauffeurId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        findEntityById(id); // vérifier existence
        pleinRepository.deleteById(id);
    }

    private PleinCarburant findEntityById(Long id) {
        return pleinRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Plein carburant introuvable avec l'ID = " + id));
    }

    private String generateReference() {
        String prefix = "FUEL-" + Year.now().getValue() + "-";
        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Long lastNumber = pleinRepository.findMaxSequenceForYear(prefix);
            long next = (lastNumber != null ? lastNumber : 0) + 1;
            String candidate = prefix + String.format("%04d", next);

            if (!pleinRepository.existsByReference(candidate)) {
                return candidate;
            }
            // collision détectée (concurrence) : on retente avec le nouveau MAX
        }

        // filet de sécurité ultime si la boucle échoue (extrêmement rare)
        return prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    @Override
    @Transactional(readOnly = true)
    public Resource getProofFile(Long id) {
        PleinCarburant plein = findEntityById(id);
        if (plein.getProofFilePath() == null) {
            throw new EntityNotFoundException("Aucun justificatif pour ce plein");
        }
        return fileStorageService.load(plein.getProofFilePath());
    }

    @Override
    public com.transport.tms.dto.fleet.response.OcrFuelResult extractFuelData(MultipartFile proof) {
        return receiptOcrService.extractFuelData(proof);
    }


}