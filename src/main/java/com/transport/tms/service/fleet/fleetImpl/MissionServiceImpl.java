package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.DepenseMission;
import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.domain.entity.fleet.PleinCarburant;
import com.transport.tms.domain.entity.fleet.SocietePartenaire;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutChauffeur;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.request.DepenseMissionRequest;
import com.transport.tms.dto.fleet.request.MissionRequest;
import com.transport.tms.dto.fleet.response.DepenseMissionResponse;
import com.transport.tms.dto.fleet.response.MissionResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.MissionMapper;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.DepenseMissionRepository;
import com.transport.tms.repository.fleet.MissionRepository;
import com.transport.tms.repository.fleet.PleinCarburantRepository;
import com.transport.tms.repository.fleet.SocietePartenaireRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.service.fleet.FileStorageService;
import com.transport.tms.service.fleet.MissionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final DepenseMissionRepository depenseMissionRepository;
    private final PleinCarburantRepository pleinCarburantRepository;
    private final SocietePartenaireRepository partenaireRepository;
    private final FileStorageService fileStorageService;
    private final MissionMapper mapper;


    private static final Set<String> ROLES_GESTION = Set.of(
            "ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_VENDEUR"
    );

    private boolean estGestion(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .anyMatch(ROLES_GESTION::contains);
    }

    private UserPrincipal utilisateurConnecte(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) {
            return up;
        }
        throw new AccessDeniedException("Utilisateur non authentifié");
    }
    @Override
    public MissionResponse create(MissionRequest request, org.springframework.web.multipart.MultipartFile letter) {
        Mission mission = mapper.toEntity(request);
        mission.setReference(generateReference());
        mission.setStatut(Mission.StatutMission.PLANNED);

        applyExecutionModeRules(mission, request);

        if (letter != null && !letter.isEmpty()) {
            String filePath = fileStorageService.store(letter);
            mission.setLetterMissionPath(filePath);
        }

        return mapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public MissionResponse update(Long id, MissionRequest request, org.springframework.web.multipart.MultipartFile letter) {
        Mission mission = findEntityById(id);
        if (mission.getStatut() != Mission.StatutMission.PLANNED) {
            throw new InvalidOperationException("Une mission ne peut être modifiée qu'en statut PLANNED");
        }
        
        mapper.updateEntity(mission, request);
        
        // Retain reference and status
        mission.setReference(mission.getReference());
        // mission.setStatut(Mission.StatutMission.PLANNED); 

        applyExecutionModeRules(mission, request);

        if (letter != null && !letter.isEmpty()) {
            String filePath = fileStorageService.store(letter);
            mission.setLetterMissionPath(filePath);
        }

        return mapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public MissionResponse uploadLetter(Long id, org.springframework.web.multipart.MultipartFile file) {
        Mission mission = findEntityById(id);
        if (mission.getStatut() != Mission.StatutMission.PLANNED && mission.getStatut() != Mission.StatutMission.IN_PROGRESS) {
            throw new InvalidOperationException("La lettre de mission ne peut être ajoutée qu'en statut PLANNED ou IN_PROGRESS");
        }
        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.store(file);
            mission.setLetterMissionPath(filePath);
        }
        return mapper.toResponse(missionRepository.save(mission));
    }

    private void applyExecutionModeRules(Mission mission, MissionRequest request) {
        String modeStr = request.modeExecution() != null ? request.modeExecution() : "INTERNAL";
        com.transport.tms.domain.enums.ModeExecution mode = com.transport.tms.domain.enums.ModeExecution.valueOf(modeStr);
        mission.setModeExecution(mode);

        if (mode == com.transport.tms.domain.enums.ModeExecution.SUBCONTRACTED) {
            mission.setVehicule(null);
            if (mission.getChauffeurSlots() != null) {
                mission.getChauffeurSlots().clear();
            }
            
            if (request.partenaireId() == null) {
                throw new InvalidOperationException("Un partenaire est requis pour une mission sous-traitée");
            }
            SocietePartenaire partenaire = partenaireRepository.findById(request.partenaireId())
                    .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable"));
            mission.setPartenaire(partenaire);
            mission.setExterneCamion(request.externeCamion());
            mission.setExterneChauffeur(request.externeChauffeur());
            
            if (mission.getStatutSousTraitance() == null) {
                mission.setStatutSousTraitance(com.transport.tms.domain.enums.StatutSousTraitance.PROPOSED);
            }

            java.math.BigDecimal taux = request.tauxCommission();
            if (taux == null && partenaire.getTauxCommissionDefaut() != null) {
                taux = partenaire.getTauxCommissionDefaut();
            }
            mission.setTauxCommission(taux);
            
            if (mission.getRevenue() != null && taux != null) {
                java.math.BigDecimal montantComm = mission.getRevenue().multiply(taux).divide(new java.math.BigDecimal("100"));
                mission.setMontantCommission(montantComm);
                mission.setMontantReversePartenaire(mission.getRevenue().subtract(montantComm));
            } else {
                mission.setMontantCommission(null);
                mission.setMontantReversePartenaire(null);
            }

        } else {
            // Mode INTERNAL
            mission.setPartenaire(null);
            mission.setTauxCommission(null);
            mission.setMontantCommission(null);
            mission.setMontantReversePartenaire(null);
            mission.setExterneCamion(null);
            mission.setExterneChauffeur(null);
            mission.setStatutSousTraitance(null);

            if (request.vehiculeId() == null) {
                throw new InvalidOperationException("Un véhicule est requis pour une mission interne");
            }
            Vehicule vehicule = findVehiculeById(request.vehiculeId());
            // Valider que si on change de vehicule ou nouvelle mission
            if (mission.getVehicule() == null || !mission.getVehicule().getId().equals(vehicule.getId())) {
                validerDisponibiliteVehicule(vehicule);
            }
            mission.setVehicule(vehicule);
            
            if (mission.getChauffeurSlots() == null) {
                mission.setChauffeurSlots(new ArrayList<>());
            }
            mission.getChauffeurSlots().clear();
            
            if (request.chauffeurs() != null) {
                for (com.transport.tms.dto.fleet.request.ChauffeurSlotRequest slotReq : request.chauffeurs()) {
                    Chauffeur c = findChauffeurById(slotReq.chauffeurId());
                    validerPermisVehicule(c);
                    com.transport.tms.domain.entity.fleet.MissionChauffeurSlot slot = new com.transport.tms.domain.entity.fleet.MissionChauffeurSlot();
                    slot.setMission(mission);
                    slot.setChauffeur(c);
                    slot.setHeureDebut(parseSlotDateTime(slotReq.heureDebut()));
                    slot.setHeureFin(parseSlotDateTime(slotReq.heureFin()));
                    mission.getChauffeurSlots().add(slot);
                }
            }
            if (mission.getChauffeurSlots().isEmpty()) {
                throw new InvalidOperationException("Au moins un chauffeur est requis pour une mission interne");
            }
        }
    }

    @Override
    public org.springframework.core.io.Resource getLetterMission(Long id) {
        Mission mission = findEntityById(id);
        if (mission.getLetterMissionPath() == null || mission.getLetterMissionPath().isBlank()) {
            throw new EntityNotFoundException("Aucune lettre de mission attachée à cette mission");
        }
        return fileStorageService.load(mission.getLetterMissionPath());
    }

    @Override
    @Transactional(readOnly = true)
    public MissionResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MissionResponse> findAll(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!estGestion(auth)) {
            throw new AccessDeniedException(
                    "Accès réservé aux rôles de gestion. Utilisez /fleet/missions/chauffeur/{chauffeurId} pour vos propres missions.");
        }

        log.info("Récupération paginée de toutes les missions");
        return missionRepository.findAll(pageable).map(mapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public List<MissionResponse> findByVehicule(Long vehiculeId) {
        return missionRepository.findByVehiculeIdOrderByPlannedDepartureDesc(vehiculeId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionResponse> findByChauffeur(Long chauffeurId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!estGestion(auth)) {
            UserPrincipal utilisateur = utilisateurConnecte(auth);
            Chauffeur chauffeurConnecte = chauffeurRepository.findByUtilisateurId(utilisateur.getId())
                    .orElseThrow(() -> new AccessDeniedException("Profil chauffeur introuvable pour cet utilisateur"));

            if (!chauffeurConnecte.getId().equals(chauffeurId)) {
                throw new AccessDeniedException("Vous ne pouvez consulter que vos propres missions");
            }
        }

        return missionRepository.findByChauffeurSlotsChauffeurIdOrderByPlannedDepartureDesc(chauffeurId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionResponse> findEnCours() {
        return missionRepository.findByStatut(Mission.StatutMission.IN_PROGRESS)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissionResponse> findEnAttenteApprobation() {
        return missionRepository.findEnAttenteApprobation()
                .stream().map(mapper::toResponse).toList();
    }


    @Override
    public MissionResponse demarrer(Long id, java.math.BigDecimal mileageAtDeparture) {
        Mission mission = findEntityById(id);
        mission.demarrer();

        // Enregistrer le kilométrage de départ si fourni
        if (mileageAtDeparture != null) {
            mission.setMileageAtDeparture(mileageAtDeparture);
            // Mettre à jour aussi le kilométrage actuel du véhicule
            mission.getVehicule().setKilometrageActuel(mileageAtDeparture);
        }

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatut(StatutVehicule.EN_MISSION);
        vehiculeRepository.save(vehicule);

        for (com.transport.tms.domain.entity.fleet.MissionChauffeurSlot slot : mission.getChauffeurSlots()) {
            Chauffeur chauffeur = slot.getChauffeur();
            chauffeur.setStatut(StatutChauffeur.EN_MISSION);
            chauffeurRepository.save(chauffeur);
        }

        log.info("Mission {} démarrée — Véhicule {} en mission (km départ: {})",
                mission.getReference(), vehicule.getReference(), mileageAtDeparture);
        return mapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public MissionResponse cloturer(Long id, java.math.BigDecimal mileageAtReturn) {
        Mission mission = findEntityById(id);

        // Enregistrer le kilométrage de retour si fourni
        if (mileageAtReturn != null) {
            mission.setMileageAtReturn(mileageAtReturn);
            // Mettre à jour le kilométrage actuel du véhicule
            mission.getVehicule().setKilometrageActuel(mileageAtReturn);
        }

        mission.cloturer();

        Vehicule vehicule = mission.getVehicule();
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehiculeRepository.save(vehicule);

        for (com.transport.tms.domain.entity.fleet.MissionChauffeurSlot slot : mission.getChauffeurSlots()) {
            Chauffeur chauffeur = slot.getChauffeur();
            chauffeur.setStatut(StatutChauffeur.DISPONIBLE);
            chauffeurRepository.save(chauffeur);
        }

        log.info("Mission {} clôturée (km retour: {})", mission.getReference(), mileageAtReturn);
        return mapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public MissionResponse annuler(Long id, String motif) {
        Mission mission = findEntityById(id);
        if (mission.getStatut() == Mission.StatutMission.COMPLETED) {
            throw new InvalidOperationException("Une mission complétée ne peut pas être annulée");
        }

        // Libérer véhicule et chauffeur si déjà en mission (MS-06)
        if (mission.getStatut() == Mission.StatutMission.IN_PROGRESS) {
            mission.getVehicule().setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(mission.getVehicule());
            for (com.transport.tms.domain.entity.fleet.MissionChauffeurSlot slot : mission.getChauffeurSlots()) {
                Chauffeur chauffeur = slot.getChauffeur();
                chauffeur.setStatut(StatutChauffeur.DISPONIBLE);
                chauffeurRepository.save(chauffeur);
            }
        }

        mission.setStatut(Mission.StatutMission.CANCELLED);
        mission.setNotes((mission.getNotes() != null ? mission.getNotes() + "\n" : "")
                + "Annulé : " + motif);
        return mapper.toResponse(missionRepository.save(mission));
    }

    @Override
    public DepenseMissionResponse addDepense(Long id, DepenseMissionRequest request, org.springframework.web.multipart.MultipartFile receipt) {
        Mission mission = findEntityById(id);
        if (mission.getStatut() == Mission.StatutMission.COMPLETED
                || mission.getStatut() == Mission.StatutMission.CANCELLED) {
            throw new InvalidOperationException(
                    "Impossible d'ajouter une dépense à une mission clôturée/annulée");
        }
        DepenseMission depense = mapper.toDepenseEntity(request);
        depense.setMission(mission);

        String filePath = null;
        if (receipt != null && !receipt.isEmpty()) {
            filePath = fileStorageService.store(receipt);
            depense.setReceiptPath(filePath);
        }

        DepenseMission saved = depenseMissionRepository.save(depense);

        // Si la dépense est de type carburant, on ajoute un plein carburant automatiquement
        if (depense.getExpenseType() == DepenseMission.TypeDepense.FUEL) {
            com.transport.tms.domain.entity.fleet.PleinCarburant plein = new com.transport.tms.domain.entity.fleet.PleinCarburant();
            plein.setReference("FUEL-" + System.currentTimeMillis());
            plein.setVehicule(mission.getVehicule());
            plein.setChauffeur(mission.getChauffeurSlots().isEmpty() ? null : mission.getChauffeurSlots().get(0).getChauffeur());
            plein.setFillingDate(depense.getExpenseDate());
            plein.setFuelType(mission.getVehicule().getTypeCarburant() != null ? mission.getVehicule().getTypeCarburant().name() : "DIESEL"); 
            plein.setQuantityLiters(request.quantityLiters() != null ? request.quantityLiters() : java.math.BigDecimal.ONE);
            plein.setPricePerLiter(request.pricePerLiter() != null ? request.pricePerLiter() : depense.getMontant());
            plein.setMileageBefore(request.mileageBefore());
            plein.setMileageAfter(request.mileageAfter());
            plein.setIsFullTank(request.isFullTank() != null ? request.isFullTank() : true);
            plein.setReceiptNumber(request.receiptNumber());
            plein.setNotes("Plein lié à la mission " + mission.getReference() + (depense.getDescription() != null ? " - " + depense.getDescription() : ""));
            plein.setProofFilePath(filePath);

            plein.calculerConsommation();
            pleinCarburantRepository.save(plein);
            
            if (request.mileageAfter() != null) {
                Vehicule vehicule = mission.getVehicule();
                vehicule.setKilometrageActuel(request.mileageAfter());
                vehiculeRepository.save(vehicule);
            }
        }

        mission.getDepenses().add(saved);
        mission.recalculerCoutTotal();
        missionRepository.save(mission);

        return mapper.toDepenseResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<DepenseMissionResponse> findDepenses(Long id) {
        findEntityById(id); // vérifier existence
        return depenseMissionRepository.findByMissionId(id)
                .stream().map(mapper::toDepenseResponse).toList();
    }

    @Override
    public void removeDepense(Long id, Long depenseId) {
        Mission mission = findEntityById(id);
        if (mission.getStatut() == Mission.StatutMission.COMPLETED) {
            throw new InvalidOperationException(
                    "Impossible de supprimer une dépense d'une mission clôturée");
        }
        depenseMissionRepository.deleteById(depenseId);

        mission.getDepenses().removeIf(d -> d.getId().equals(depenseId));
        mission.recalculerCoutTotal();
        missionRepository.save(mission);
    }

    @Override
    public org.springframework.core.io.Resource getDepenseReceipt(Long id, Long depenseId) {
        DepenseMission depense = depenseMissionRepository.findById(depenseId)
                .orElseThrow(() -> new EntityNotFoundException("Dépense introuvable"));
        if (depense.getReceiptPath() == null || depense.getReceiptPath().isBlank()) {
            throw new EntityNotFoundException("Aucun justificatif attaché à cette dépense");
        }
        return fileStorageService.load(depense.getReceiptPath());
    }

    // ── Validations métier ────────────────────────────────────

    private void validerDisponibiliteVehicule(Vehicule vehicule) {
        if (vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
            throw new InvalidOperationException(
                    "Le véhicule " + vehicule.getReference()
                            + " n'est pas disponible (statut: " + vehicule.getStatut() + ")");
        }
    }

    private void validerDisponibiliteChauffeur(Chauffeur chauffeur) {
        if (chauffeur.getStatut() != StatutChauffeur.DISPONIBLE) {
            throw new InvalidOperationException(
                    "Le chauffeur " + chauffeur.getNom() + " " + chauffeur.getPrenom()
                            + " n'est pas disponible (statut: " + chauffeur.getStatut() + ")");
        }
    }

    private void validerPermisVehicule(Chauffeur chauffeur) {
        if (chauffeur.getDateExpirationPermis() != null
                && chauffeur.getDateExpirationPermis().isBefore(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Le permis du chauffeur " + chauffeur.getNom()
                            + " " + chauffeur.getPrenom() + " est expiré");
        }
    }

    // ── Méthodes internes ─────────────────────────────────────

    private Mission findEntityById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Mission introuvable avec l'ID = " + id));
    }

    private Vehicule findVehiculeById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Véhicule introuvable avec l'ID = " + id));
    }

    private java.time.LocalDateTime parseSlotDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // Gérer "yyyy-MM-dd'T'HH:mm" (16 chars) ou "yyyy-MM-dd'T'HH:mm:ss"
            if (s.length() == 16) s = s + ":00";
            return java.time.LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            log.warn("Impossible de parser la date/heure du créneau : {}", s);
            return null;
        }
    }

    private Chauffeur findChauffeurById(Long id) {
        return chauffeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chauffeur introuvable avec l'ID = " + id));
    }

    private String generateReference() {
        long count = missionRepository.count() + 1;
        return String.format("MSN-%d-%04d", LocalDate.now().getYear(), count);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MissionResponse> findMesMissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("findMesMissions - User: {}, Authorities: {}", 
                auth != null ? auth.getName() : "null",
                auth != null ? auth.getAuthorities() : "null");

        // Admin / SuperAdmin → toutes les missions
        if (estGestion(auth)) {
            return missionRepository.findAll().stream()
                    .map(mapper::toResponse).toList();
        }

        // Chauffeur → ses propres missions uniquement
        UserPrincipal utilisateur = utilisateurConnecte(auth);
        Chauffeur chauffeur = chauffeurRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new AccessDeniedException("Aucun profil chauffeur lié à cet utilisateur"));

        return missionRepository.findByChauffeurSlotsChauffeurIdOrderByPlannedDepartureDesc(chauffeur.getId())
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepenseMissionResponse> findAllTolls(Pageable pageable) {
        return depenseMissionRepository.findByExpenseTypeOrderByExpenseDateDesc(
                DepenseMission.TypeDepense.TOLL, pageable).map(mapper::toDepenseResponse);
    }
}