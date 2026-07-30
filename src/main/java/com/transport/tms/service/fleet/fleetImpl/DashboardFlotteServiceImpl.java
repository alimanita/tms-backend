package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.domain.entity.fleet.NotificationFlotte;
import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.response.DashboardOverviewResponse;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import com.transport.tms.mapper.fleet.VehiculeMapper;
import com.transport.tms.repository.fleet.*;
import com.transport.tms.service.fleet.DashboardFlotteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardFlotteServiceImpl implements DashboardFlotteService {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;
    private final OrdreTravailRepository ordreTravailRepository;
    private final PleinCarburantRepository pleinCarburantRepository;
    private final MissionRepository missionRepository;
    private final NotificationFlotteRepository notificationFlotteRepository;

    private static final int TOP_VEHICULES_LIMIT = 5;

    @Override
    public DashboardOverviewResponse getOverview() {
        int annee = LocalDate.now().getYear();
        int mois = LocalDate.now().getMonthValue();

        long totalVehicules = vehiculeRepository.count();
        long disponibles = vehiculeRepository.findByStatutAndActifTrue(StatutVehicule.DISPONIBLE).size();
        long enMission = vehiculeRepository.findByStatutAndActifTrue(StatutVehicule.EN_MISSION).size();
        long horsService = vehiculeRepository.findByStatutAndActifTrue(StatutVehicule.HS).size();
        // ⚠️ "vehiculesEnMaintenance" suppose un statut dédié — adapte si ton enum StatutVehicule
        // n'a que DISPONIBLE/EN_SERVICE/HORS_SERVICE (dans ce cas mets 0 ou réutilise HORS_SERVICE)
        long enMaintenance = 0L;

        BigDecimal coutCarburantMois = pleinCarburantRepository.sumCoutCarburantMois(annee, mois);
        BigDecimal coutMaintenanceMois = ordreTravailRepository.sumCoutMois(annee, mois);
        BigDecimal coutTotalMois = coutCarburantMois.add(coutMaintenanceMois);

        BigDecimal coutMoyenParKm = calculerCoutMoyenParKm(coutTotalMois);

        // ⚠️ suppose un statut Mission.StatutMission.IN_PROGRESS / PLANNED — à confirmer
        long missionsEnCours = missionRepository.countByStatut(Mission.StatutMission.IN_PROGRESS);
        long missionsEnAttente = missionRepository.countByStatut(Mission.StatutMission.PLANNED);

        // ⚠️ suppose NotificationFlotte.Severite.CRITICAL / WARNING
        long alertesCritiques = notificationFlotteRepository.countBySeverityAndIsReadFalseAndIsDismissedFalse(
                NotificationFlotte.Severite.CRITICAL);
        long alertesWarning = notificationFlotteRepository.countBySeverityAndIsReadFalseAndIsDismissedFalse(
                NotificationFlotte.Severite.WARNING);

        List<VehiculeResponse> topVehiculesCouteux = getTopVehiculesParCout(TOP_VEHICULES_LIMIT);

        return new DashboardOverviewResponse(
                totalVehicules,
                disponibles,
                enMission,
                enMaintenance,
                horsService,
                coutCarburantMois,
                coutMaintenanceMois,
                coutTotalMois,
                coutMoyenParKm,
                missionsEnCours,
                missionsEnAttente,
                alertesCritiques,
                alertesWarning,
                topVehiculesCouteux
        );
    }

    @Override
    public List<VehiculeResponse> getTopVehiculesParCout(int limit) {
        List<Vehicule> vehicules = vehiculeRepository.findByActifTrue(Pageable.unpaged()).getContent();

        return vehicules.stream()
                .map(v -> Map.entry(v, ordreTravailRepository.sumCoutByEntity(
                        OrdreTravail.TypeEntite.VEHICLE, v.getId())))
                .sorted(Map.Entry.<Vehicule, BigDecimal>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> vehiculeMapper.toResponse(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCoutsMensuels(int mois) {
        int annee = LocalDate.now().getYear();

        BigDecimal coutMaintenance = ordreTravailRepository.sumCoutMois(annee, mois);
        BigDecimal coutCarburant = pleinCarburantRepository.sumCoutCarburantMois(annee, mois);

        Map<String, Object> result = new HashMap<>();
        result.put("mois", mois);
        result.put("annee", annee);
        result.put("coutMaintenance", coutMaintenance);
        result.put("coutCarburant", coutCarburant);
        result.put("coutTotal", coutMaintenance.add(coutCarburant));
        return result;
    }

    @Override
    public Map<String, Object> getConsommationCarburant(int mois) {
        int annee = LocalDate.now().getYear();

        BigDecimal coutCarburant = pleinCarburantRepository.sumCoutCarburantMois(annee, mois);

        Map<String, Object> result = new HashMap<>();
        result.put("mois", mois);
        result.put("annee", annee);
        result.put("coutCarburant", coutCarburant);
        return result;
    }

    private BigDecimal calculerCoutMoyenParKm(BigDecimal coutTotalMois) {
        // ⚠️ Placeholder : somme du kilométrage parcouru sur le mois non disponible directement.
        // À remplacer par une vraie requête (ex: somme des distanceSinceLast du mois sur PleinCarburant,
        // ou différence de kilométrageActuel sur la période).
        BigDecimal kmTotalEstime = BigDecimal.ONE; // éviter division par zéro en attendant
        if (kmTotalEstime.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return coutTotalMois.divide(kmTotalEstime, 2, RoundingMode.HALF_UP);
    }
}