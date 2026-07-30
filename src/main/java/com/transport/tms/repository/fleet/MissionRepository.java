package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    Optional<Mission> findByReference(String reference);

    boolean existsByReference(String reference);

    // Par statut
    Page<Mission> findByStatut(Mission.StatutMission statut, Pageable pageable);

    List<Mission> findByStatut(Mission.StatutMission statut);

    List<Mission> findByStatutIn(List<Mission.StatutMission> statuts);

    // Par véhicule
    List<Mission> findByVehiculeIdOrderByPlannedDepartureDesc(Long vehiculeId);

    Page<Mission> findByVehiculeId(Long vehiculeId, Pageable pageable);

    // Par chauffeur
    List<Mission> findByChauffeurIdOrderByPlannedDepartureDesc(Long chauffeurId);

    Page<Mission> findByChauffeurId(Long chauffeurId, Pageable pageable);

    // Missions en cours pour un véhicule (vérification disponibilité)
    boolean existsByVehiculeIdAndStatutIn(
            Long vehiculeId, List<Mission.StatutMission> statuts);

    // Missions en cours pour un chauffeur
    boolean existsByChauffeurIdAndStatutIn(
            Long chauffeurId, List<Mission.StatutMission> statuts);

    // Missions entre deux dates
    @Query("""
            SELECT m FROM Mission m
            WHERE m.plannedDeparture BETWEEN :debut AND :fin
            ORDER BY m.plannedDeparture ASC
            """)
    List<Mission> findEntre(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Missions en retard (IN_PROGRESS dépassant la date retour prévue)
    @Query("""
            SELECT m FROM Mission m
            WHERE m.statut = 'IN_PROGRESS'
            AND m.plannedReturn < :now
            """)
    List<Mission> findEnRetard(@Param("now") LocalDateTime now);

    // Coût total missions du mois
    @Query("""
            SELECT COALESCE(SUM(m.totalCost), 0)
            FROM Mission m
            WHERE m.statut = 'COMPLETED'
            AND FUNCTION('YEAR', m.actualReturn) = :annee
            AND FUNCTION('MONTH', m.actualReturn) = :mois
            """)
    BigDecimal sumCoutMissions(
            @Param("annee") int annee,
            @Param("mois") int mois);

    // Comptage par statut
    long countByStatut(Mission.StatutMission statut);

    // Missions en attente d'approbation
    @Query("""
            SELECT m FROM Mission m
            WHERE m.statut = 'PLANNED'
            ORDER BY m.plannedDeparture ASC
            """)
    List<Mission> findEnAttenteApprobation();
    long countByChauffeurIdAndStatut(Long chauffeurId, Mission.StatutMission statut);

    long countByChauffeurIdAndStatutAndActualReturnBetween(
            Long chauffeurId, Mission.StatutMission statut,
            LocalDateTime debut, LocalDateTime fin);
}