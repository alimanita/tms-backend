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
    List<Mission> findByChauffeursIdOrderByPlannedDepartureDesc(Long chauffeurId);

    Page<Mission> findByChauffeursId(Long chauffeurId, Pageable pageable);

    // Missions en cours pour un véhicule (vérification disponibilité)
    boolean existsByVehiculeIdAndStatutIn(
            Long vehiculeId, List<Mission.StatutMission> statuts);

    // Missions en cours pour un chauffeur
    boolean existsByChauffeursIdAndStatutIn(
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
    long countByChauffeursIdAndStatut(Long chauffeurId, Mission.StatutMission statut);

    long countByChauffeursIdAndStatutAndActualReturnBetween(
            Long chauffeurId, Mission.StatutMission statut,
            LocalDateTime debut, LocalDateTime fin);

    // --- Aggregations pour les Rapports ---
    @Query("SELECT CONCAT(c.prenom, ' ', c.nom), COUNT(m) FROM Mission m JOIN m.chauffeurs c GROUP BY c.id, c.prenom, c.nom")
    List<Object[]> countMissionsByDriver();

    @Query("SELECT m.statut, COUNT(m) FROM Mission m GROUP BY m.statut")
    List<Object[]> countMissionsByStatus();

    @Query("SELECT CONCAT(c.prenom, ' ', c.nom), SUM(m.mileageAtReturn - m.mileageAtDeparture) FROM Mission m JOIN m.chauffeurs c WHERE m.mileageAtReturn IS NOT NULL AND m.mileageAtDeparture IS NOT NULL GROUP BY c.id, c.prenom, c.nom")
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
    
    @Query("SELECT COALESCE(SUM(m.tollCost), 0) FROM Mission m WHERE m.statut = 'COMPLETED' AND m.tollCost IS NOT NULL")
    BigDecimal sumAllTollCost();

    @Query("SELECT COALESCE(SUM(m.fuelCost), 0) FROM Mission m WHERE m.statut = 'COMPLETED' AND m.fuelCost IS NOT NULL")
    BigDecimal sumAllFuelCost();

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
    @Query("""
        SELECT EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn), COALESCE(SUM(m.totalCost - COALESCE(m.fuelCost, 0) - COALESCE(m.tollCost, 0)), 0)
        FROM Mission m
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND m.totalCost IS NOT NULL
        AND m.actualReturn >= :fromDate
        GROUP BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        ORDER BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") LocalDateTime fromDate);

    // ── RAPPORT CHAUFFEUR : revenus & coûts par mois pour un chauffeur ─────────
    @Query("""
        SELECT EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn),
               COALESCE(SUM(m.revenue), 0), COALESCE(SUM(m.totalCost), 0), COUNT(m)
        FROM Mission m
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND (:chauffeurId IS NULL OR EXISTS (SELECT 1 FROM m.chauffeurs c WHERE c.id = :chauffeurId))
        AND m.actualReturn BETWEEN :debut AND :fin
        GROUP BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        ORDER BY EXTRACT(YEAR FROM m.actualReturn), EXTRACT(MONTH FROM m.actualReturn)
        """)
    List<Object[]> statsChauffeurParMois(
            @Param("chauffeurId") Long chauffeurId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── NOUVEAU RAPPORT CHAUFFEUR (Période Unique) ─────────────────────────────
    @Query("""
        SELECT c.id, c.nom, c.prenom, c.valeurSalaire, c.typeSalaire,
               COALESCE(SUM(m.revenue), 0), COALESCE(SUM(m.totalCost), 0), COUNT(m)
        FROM Mission m
        JOIN m.chauffeurs c
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND m.actualReturn BETWEEN :debut AND :fin
        GROUP BY c.id, c.nom, c.prenom, c.valeurSalaire, c.typeSalaire
        ORDER BY c.nom ASC
        """)
    List<Object[]> statsTousChauffeursSurPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT m.id, m.reference, m.actualReturn, m.revenue, m.totalCost
        FROM Mission m
        JOIN m.chauffeurs c
        WHERE m.statut = 'COMPLETED'
        AND m.actualReturn IS NOT NULL
        AND c.id = :chauffeurId
        AND m.actualReturn BETWEEN :debut AND :fin
        ORDER BY m.actualReturn DESC
        """)
    List<Object[]> missionsChauffeurSurPeriode(
            @Param("chauffeurId") Long chauffeurId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── RAPPORT AMAZON filtré par période : revenus par mois ──────────────────
    @Query("""
        SELECT EXTRACT(MONTH FROM m.actualReturn), COALESCE(SUM(m.revenue), 0)
        FROM Mission m
        WHERE m.statut = 'COMPLETED' AND m.actualReturn IS NOT NULL
        AND m.actualReturn BETWEEN :debut AND :fin
        GROUP BY EXTRACT(MONTH FROM m.actualReturn)
        ORDER BY EXTRACT(MONTH FROM m.actualReturn)
        """)
    List<Object[]> sumRevenueByMonthBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("""
        SELECT EXTRACT(YEAR FROM m.actualReturn), COALESCE(SUM(m.revenue), 0)
        FROM Mission m
        WHERE m.statut = 'COMPLETED' AND m.actualReturn IS NOT NULL
        AND EXTRACT(YEAR FROM m.actualReturn) BETWEEN :anDebut AND :anFin
        GROUP BY EXTRACT(YEAR FROM m.actualReturn)
        ORDER BY EXTRACT(YEAR FROM m.actualReturn)
        """)
    List<Object[]> sumRevenueByYearBetween(
            @Param("anDebut") int anDebut,
            @Param("anFin") int anFin);

}
