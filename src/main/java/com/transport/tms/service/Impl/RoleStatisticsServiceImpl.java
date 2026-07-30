package com.transport.tms.service.Impl;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.statistique.RoleDashboardStatsDto;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.MissionRepository;
import com.transport.tms.repository.fleet.OrdreTravailRepository;
import com.transport.tms.repository.fleet.PleinCarburantRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.RoleStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleStatisticsServiceImpl implements RoleStatisticsService {

    // ── Fleet ─────────────────────────────────────────────────────
    private final UtilisateurRepository    utilisateurRepository;
    private final ChauffeurRepository      chauffeurRepository;
    private final VehiculeRepository       vehiculeRepository;
    private final MissionRepository        missionRepository;
    private final OrdreTravailRepository   ordreTravailRepository;
    private final PleinCarburantRepository pleinCarburantRepository;

    @Autowired
    public RoleStatisticsServiceImpl(
            UtilisateurRepository    utilisateurRepository,
            ChauffeurRepository      chauffeurRepository,
            VehiculeRepository       vehiculeRepository,
            MissionRepository        missionRepository,
            OrdreTravailRepository   ordreTravailRepository,
            PleinCarburantRepository pleinCarburantRepository
    ) {
        this.utilisateurRepository    = utilisateurRepository;
        this.chauffeurRepository      = chauffeurRepository;
        this.vehiculeRepository       = vehiculeRepository;
        this.missionRepository        = missionRepository;
        this.ordreTravailRepository   = ordreTravailRepository;
        this.pleinCarburantRepository = pleinCarburantRepository;
    }

    @Override
    public RoleDashboardStatsDto computeStats(Integer idEntreprise, String username) {

        log.info("Computing role dashboard stats for entreprise {} / user {}", idEntreprise, username);

        LocalDateTime debutMoisLdt = LocalDateTime.of(YearMonth.now().atDay(1), LocalTime.MIN);
        LocalDateTime finMoisLdt   = LocalDateTime.of(YearMonth.now().atEndOfMonth(), LocalTime.of(23, 59, 59));

        // Utilisateur connecté
        Utilisateur utilisateur = username != null
                ? utilisateurRepository.findByEmailOrUsername(username, username).orElse(null)
                : null;

        // ════════════════════════ CHAUFFEUR ════════════════════════
        long mesVehicules         = 0;
        long vehiculesDisponibles = 0;
        long missionsEnCours      = 0;
        long maintenancesAVenir   = 0;
        long missionsTerminees    = 0;
        long pleinsCeMois         = 0;
        BigDecimal coutCarburantMois = BigDecimal.ZERO;

        if (utilisateur != null) {
            Chauffeur chauffeur = chauffeurRepository
                    .findByUtilisateurId(utilisateur.getId())
                    .orElse(null);

            if (chauffeur != null) {
                mesVehicules = vehiculeRepository.countByChauffeurAffecteId(chauffeur.getId());

                List<Long> vehiculeIds = vehiculeRepository.findIdsByChauffeurAffecteId(chauffeur.getId());

                if (!vehiculeIds.isEmpty()) {
                    vehiculesDisponibles = vehiculeRepository
                            .countByChauffeurAffecteIdAndStatut(chauffeur.getId(), StatutVehicule.DISPONIBLE);
                    maintenancesAVenir = ordreTravailRepository.countByEntityTypeAndEntityIdInAndStatut(
                            OrdreTravail.TypeEntite.VEHICLE, vehiculeIds, OrdreTravail.StatutOT.PLANNED);
                }

                missionsEnCours = missionRepository
                        .countByChauffeurIdAndStatut(chauffeur.getId(), Mission.StatutMission.IN_PROGRESS);

                missionsTerminees = missionRepository
                        .countByChauffeurIdAndStatutAndActualReturnBetween(
                                chauffeur.getId(), Mission.StatutMission.COMPLETED,
                                debutMoisLdt, finMoisLdt);

                pleinsCeMois = pleinCarburantRepository
                        .countByChauffeurIdAndFillingDateBetween(chauffeur.getId(), debutMoisLdt, finMoisLdt);

                BigDecimal coutCarb = pleinCarburantRepository
                        .sumCoutByChauffeurAndPeriode(chauffeur.getId(), debutMoisLdt, finMoisLdt);
                coutCarburantMois = coutCarb != null ? coutCarb : BigDecimal.ZERO;
            }
        }

        RoleDashboardStatsDto stats = RoleDashboardStatsDto.builder()
                .idEntreprise(idEntreprise)
                .mesVehicules(mesVehicules)
                .vehiculesDisponibles(vehiculesDisponibles)
                .missionsEnCours(missionsEnCours)
                .maintenancesAVenir(maintenancesAVenir)
                .missionsTerminees(missionsTerminees)
                .pleinsCeMois(pleinsCeMois)
                .coutCarburantMois(coutCarburantMois)
                .build();

        log.info("Stats computed: {}", stats);
        return stats;
    }
}