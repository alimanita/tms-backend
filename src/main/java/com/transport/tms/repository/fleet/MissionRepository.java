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
            AND EXTRACT(YEAR FROM m.actualReturn) = :annee
            AND EXTRACT(MONTH FROM m.actualReturn) = :mois
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

    // --- Aggregations pour les Rapports ---
    @Query("SELECT CONCAT(c.prenom, ' ', c.nom), COUNT(m) FROM Mission m JOIN m.chauffeur c GROUP BY c.id, c.prenom, c.nom")
    List<Object[]> countMissionsByDriver();

    @Query("SELECT m.statut, COUNT(m) FROM Mission m GROUP BY m.statut")
    List<Object[]> countMissionsByStatus();

    @Query("SELECT CONCAT(c.prenom, ' ', c.nom), SUM(m.mileageAtReturn - m.mileageAtDeparture) FROM Mission m JOIN m.chauffeur c WHERE m.mileageAtReturn IS NOT NULL AND m.mileageAtDeparture IS NOT NULL GROUP BY c.id, c.prenom, c.nom")
    List<Object[]> sumMileageByDriver();

    @Query("SELECT EXTRACT(MONTH FROM m.actualReturn), SUM(m.revenue) FROM Mission m WHERE m.statut = 'COMPLETED' AND m.actualReturn IS NOT NULL GROUP BY EXTRACT(MONTH FROM m.actualReturn)")
    List<Object[]> sumRevenueByMonth();

    // --- Dashboard KPIs ---

    /** Somme de tous les revenus des missions clôturées (tout le temps). */
    @Query("SELECT COALESCE(SUM(m.revenue), 0) FROM Mission m WHERE m.statut = 'COMPLETED' AND m.revenue IS NOT NULL")
    BigDecimal sumAllRevenue();

    /** Somme des coûts de toutes les missions clôturées (tout le temps). */
    @Query("SELECT COALESCE(SUM(m.totalCost), 0) FROM Mission m WHERE m.statut = 'COMPLETED' AND m.totalCost IS NOT NULL")
    BigDecimal sumAllMissionCost();

    /** Revenus mensuels (6 derniers mois) pour le graphique du dashboard. */
    @Query("""
        SELECT EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn), COALESCE(SUM(m.revenue), 0)
        FROM Mission m
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND m.actualReturn >= :fromDate
        GROUP BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        ORDER BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        """)
    List<Object[]> sumRevenueByYearMonth(@Param("fromDate") LocalDateTime fromDate);
    /** Coûts missions mensuels (6 derniers mois) pour le graphique du dashboard. */
    @Query("""
        SELECT EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn), COALESCE(SUM(m.totalCost), 0)
        FROM Mission m
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND m.totalCost IS NOT NULL
        AND m.actualReturn >= :fromDate
        GROUP BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        ORDER BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") LocalDateTime fromDate);


}