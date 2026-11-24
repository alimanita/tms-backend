package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.VehiculeMapper;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.service.fleet.VehiculeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;
    private final ChauffeurRepository chauffeurRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final int MAX_TENTATIVES_REFERENCE = 5;

    @Override
    public VehiculeResponse create(VehiculeRequest request) {
        String reference;
        if (request.reference() != null && !request.reference().isBlank()) {
            reference = request.reference();
            if (vehiculeRepository.existsByReference(reference)) {
                throw new InvalidOperationException(
                        "La reference '" + reference + "' existe deja. Merci d'en choisir une autre.");
            }
        } else {
            reference = genererReferenceUnique();
        }

        if (vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new InvalidOperationException(
                    "Un vehicule avec l'immatriculation '" + request.immatriculation() + "' existe deja");
        }

        Vehicule vehicule = vehiculeMapper.toEntity(request);
        vehicule.setReference(reference);

        // Affectation chauffeur lors de la creation
        if (request.chauffeurAffecteId() != null) {
            chauffeurRepository.findById(request.chauffeurAffecteId())
                    .ifPresent(vehicule::setChauffeurAffecte);
        }

        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    private String genererReferenceUnique() {
        String reference;
        int tentative = 0;
        do {
            reference = genererReference();
            tentative++;
            if (tentative > MAX_TENTATIVES_REFERENCE) {
                throw new IllegalStateException(
                        "Impossible de generer une reference vehicule unique apres "
                                + MAX_TENTATIVES_REFERENCE + " tentatives");
            }
        } while (vehiculeRepository.existsByReference(reference));
        return reference;
    }

    private String genererReference() {
        int annee = LocalDate.now().getYear();
        int prochainNumero = vehiculeRepository.findLastReferenceForYear(annee)
                .map(this::extraireNumeroSuivant)
                .orElse(1);
        return String.format("VH-%d-%03d", annee, prochainNumero);
    }

    private int extraireNumeroSuivant(String derniereReference) {
        try {
            String[] parts = derniereReference.split("-");
            int dernierNumero = Integer.parseInt(parts[parts.length - 1]);
            return dernierNumero + 1;
        } catch (Exception e) {
            log.warn("Impossible de parser la reference '{}', fallback sur count()", derniereReference);
            return (int) vehiculeRepository.count() + 1;
        }
    }

    @Override
    public VehiculeResponse update(Long id, VehiculeRequest request) {
        Vehicule vehicule = findEntityById(id);
        if (vehicule.getStatut() == StatutVehicule.HS) {
            throw new InvalidOperationException(
                    "Un vehicule hors service ne peut pas etre modifie");
        }
        vehiculeMapper.updateEntity(vehicule, request);

        // Gestion de l'affectation chauffeur
        if (request.chauffeurAffecteId() != null) {
            chauffeurRepository.findById(request.chauffeurAffecteId())
                    .ifPresent(vehicule::setChauffeurAffecte);
        } else {
            vehicule.setChauffeurAffecte(null);
        }

        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse findById(Long id) {
        return vehiculeMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculeResponse> findAll(Pageable pageable) {
        // Tous les roles peuvent voir tous les vehicules (utile pour les formulaires)
        return vehiculeRepository.findAll(pageable)
                .map(vehiculeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findDisponibles() {
        return vehiculeRepository
                .findByStatutAndActifTrue(StatutVehicule.DISPONIBLE)
                .stream()
                .map(vehiculeMapper::toResponse)
                .toList();
    }

    @Override
    public VehiculeResponse updateStatut(Long id, String statut) {
        Vehicule vehicule = findEntityById(id);
        StatutVehicule nouveauStatut;
        try {
            nouveauStatut = StatutVehicule.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException(
                    "Statut invalide : " + statut
                            + ". Valeurs acceptees : DISPONIBLE, EN_MISSION, EN_MAINTENANCE, HS");
        }
        vehicule.setStatut(nouveauStatut);
        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = findEntityById(id);
        if (vehicule.getStatut() == StatutVehicule.EN_MISSION) {
            throw new InvalidOperationException(
                    "Impossible de desactiver un vehicule en mission");
        }
        vehicule.setActif(false);
        vehiculeRepository.save(vehicule);
    }

    @Override
    public VehiculeResponse toggleActif(Long id) {
        log.info("Toggle actif du vehicule ID: {}", id);
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicule introuvable, id=" + id));

        vehicule.setActif(!Boolean.TRUE.equals(vehicule.getActif()));
        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse findMonVehicule() {
        Chauffeur chauffeur = resolveChauffeurFromConnectedUser();
        if (chauffeur == null) return null;

        Page<Vehicule> page = vehiculeRepository.findByChauffeurAffecteId(
                chauffeur.getId(), PageRequest.of(0, 1));
        return page.isEmpty() ? null : vehiculeMapper.toResponse(page.getContent().get(0));
    }

    private Chauffeur resolveChauffeurFromConnectedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        boolean isChauffeur = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CHAUFFEUR"));
        if (!isChauffeur) return null;
        String username = auth.getName();
        return utilisateurRepository.findByEmailOrUsername(username, username)
                .flatMap(u -> chauffeurRepository.findByUtilisateurId(u.getId()))
                .orElse(null);
    }

    private Vehicule findEntityById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vehicule introuvable avec l'ID = " + id));
    }

    @Transactional(readOnly = true)
    public long countByChauffeurAffecte(Long chauffeurId) {
        return vehiculeRepository.countByChauffeurAffecteId(chauffeurId);
    }

    @Transactional(readOnly = true)
    public long countByChauffeurAffecteAndStatut(Long chauffeurId, StatutVehicule statut) {
        return vehiculeRepository.countByChauffeurAffecteIdAndStatut(chauffeurId, statut);
    }
}
