package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.OrdreTravail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdreTravailRepository extends JpaRepository<OrdreTravail, Long> {

    Optional<OrdreTravail> findByReference(String reference);

    boolean existsByReference(String reference);

    // Par entité
    List<OrdreTravail> findByEntityTypeAndEntityId(
            OrdreTravail.TypeEntite entityType, Long entityId);

    Page<OrdreTravail> findByEntityTypeAndEntityId(
            OrdreTravail.TypeEntite entityType, Long entityId, Pageable pageable);

    // Par statut
    List<OrdreTravail> findByStatut(OrdreTravail.StatutOT statut);

    Page<OrdreTravail> findByStatut(OrdreTravail.StatutOT statut, Pageable pageable);

    // Par priorité
    List<OrdreTravail> findByPrioriteAndStatutNot(
            OrdreTravail.PrioriteOT priorite, OrdreTravail.StatutOT statut);

    // OTs planifiés entre deux dates
    @Query("""
            SELECT o FROM OrdreTravail o
            WHERE o.scheduledDate BETWEEN :debut AND :fin
            AND o.statut NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY o.scheduledDate ASC
            """)
    List<OrdreTravail> findPlanifiesEntre(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    // OTs à venir (dans les 30 prochains jours)
    @Query("""
            SELECT o FROM OrdreTravail o
            WHERE o.scheduledDate BETWEEN :today AND :limit
            AND o.statut NOT IN ('COMPLETED', 'CANCELLED')
            ORDER BY o.scheduledDate ASC
            """)
    List<OrdreTravail> findAVenir(
            @Param("today") LocalDate today,
            @Param("limit") LocalDate limit);

    // OTs en cours
    List<OrdreTravail> findByStatutIn(List<OrdreTravail.StatutOT> statuts);

    // Coût total maintenance par entité
    @Query("""
            SELECT COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
            FROM OrdreTravail o
            WHERE o.entityType = :entityType
            AND o.entityId = :entityId
            AND o.statut = 'COMPLETED'
            """)
    java.math.BigDecimal sumCoutByEntity(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("entityId") Long entityId);

    // Coût maintenance du mois courant
    @Query("""
            SELECT COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
            FROM OrdreTravail o
            WHERE o.statut = 'COMPLETED'
            AND FUNCTION('YEAR', o.completedAt) = :annee
            AND FUNCTION('MONTH', o.completedAt) = :mois
            """)
    java.math.BigDecimal sumCoutMois(
            @Param("annee") int annee,
            @Param("mois") int mois);

    // Comptage par statut
    long countByStatut(OrdreTravail.StatutOT statut);
    long countByEntityTypeAndTechnicianIdAndStatut(
            OrdreTravail.TypeEntite entityType, Long technicianId, OrdreTravail.StatutOT statut);

    long countByEntityTypeAndTechnicianIdAndStatutAndCompletedAtBetween(
            OrdreTravail.TypeEntite entityType, Long technicianId, OrdreTravail.StatutOT statut,
            LocalDateTime debut, LocalDateTime fin);

    @Query("""
            SELECT COUNT(DISTINCT ot.entityId) FROM OrdreTravail ot
            WHERE ot.entityType = :entityType AND ot.technicianId = :technicianId
            """)
    long countDistinctEntityIdByEntityTypeAndTechnicianId(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("technicianId") Long technicianId);

    @Query("""
            SELECT DISTINCT ot.entityId FROM OrdreTravail ot
            WHERE ot.entityType = :entityType AND ot.technicianId = :technicianId
            """)
    List<Long> findDistinctEntityIdsByEntityTypeAndTechnicianId(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("technicianId") Long technicianId);

    // ── CHAUFFEUR (maintenances véhicules à venir) ───────────────────────
    long countByEntityTypeAndEntityIdInAndStatut(
            OrdreTravail.TypeEntite entityType, List<Long> entityIds, OrdreTravail.StatutOT statut);

    // ── MECANICIEN (OT planifiés ce mois) ─────────────────────────────────
    long countByEntityTypeAndTechnicianIdAndStatutAndScheduledDateBetween(
            OrdreTravail.TypeEntite entityType, Long technicianId, OrdreTravail.StatutOT statut,
            LocalDate debut, LocalDate fin);

    // ── MECANICIEN (coût maintenance ce mois pour ses machines) ───────────
    @Query("""
            SELECT COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
            FROM OrdreTravail o
            WHERE o.entityType = :entityType
            AND o.technicianId = :technicianId
            AND o.statut = 'COMPLETED'
            AND o.completedAt BETWEEN :debut AND :fin
            """)
    java.math.BigDecimal sumCoutByTechnicianAndPeriode(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("technicianId") Long technicianId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── MECANICIEN : stats globales (sans filtre technicien) ──────────────
    long countByEntityTypeAndStatut(
            OrdreTravail.TypeEntite entityType, OrdreTravail.StatutOT statut);

    long countByEntityTypeAndStatutAndCompletedAtBetween(
            OrdreTravail.TypeEntite entityType, OrdreTravail.StatutOT statut,
            LocalDateTime debut, LocalDateTime fin);

    long countByEntityTypeAndStatutAndScheduledDateBetween(
            OrdreTravail.TypeEntite entityType, OrdreTravail.StatutOT statut,
            LocalDate debut, LocalDate fin);

    @Query("""
            SELECT COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
            FROM OrdreTravail o
            WHERE o.entityType = :entityType
            AND o.statut = 'COMPLETED'
            AND o.completedAt BETWEEN :debut AND :fin
            """)
    java.math.BigDecimal sumCoutByEntityTypeAndPeriode(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── RAPPORTS ENTRETIENS : agrégation par mois ──────────────────────────
    @Query(value = """
        SELECT
            EXTRACT(YEAR  FROM COALESCE(o.completed_at, o.updated_at))::int  AS annee,
            EXTRACT(MONTH FROM COALESCE(o.completed_at, o.updated_at))::int  AS mois,
            COALESCE(SUM(COALESCE(ml.hours_actual, ml.hours_planned) * ml.hourly_rate), 0)          AS cout_main_oeuvre,
            COALESCE(SUM(COALESCE(ms.quantity_used, ms.quantity_planned) * ms.unit_cost), 0)        AS cout_pieces,
            COALESCE(SUM(COALESCE(ml.hours_actual, ml.hours_planned) * ml.hourly_rate), 0)
            + COALESCE(SUM(COALESCE(ms.quantity_used, ms.quantity_planned) * ms.unit_cost), 0)      AS cout_total,
            COUNT(DISTINCT o.id)                                                                    AS nombre_ot
        FROM ordre_travail o
        LEFT JOIN maintenance_labor      ml ON ml.maintenance_order_id = o.id
        LEFT JOIN maintenance_spare_part ms ON ms.maintenance_order_id = o.id
        WHERE o.statut = 'COMPLETED'
        AND (:entityType IS NULL OR o.entity_type = :entityType)
        AND COALESCE(o.completed_at, o.updated_at) BETWEEN :debut AND :fin
        GROUP BY EXTRACT(YEAR  FROM COALESCE(o.completed_at, o.updated_at)),
                 EXTRACT(MONTH FROM COALESCE(o.completed_at, o.updated_at))
        ORDER BY EXTRACT(YEAR  FROM COALESCE(o.completed_at, o.updated_at)),
                 EXTRACT(MONTH FROM COALESCE(o.completed_at, o.updated_at))
        """, nativeQuery = true)
    List<Object[]> aggregateParMoisNative(
            @Param("entityType") String entityType,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Agrégation par année
    @Query(value = """
        SELECT
            EXTRACT(YEAR FROM COALESCE(o.completed_at, o.updated_at))::int              AS annee,
            COALESCE(SUM(COALESCE(ml.hours_actual, ml.hours_planned) * ml.hourly_rate), 0)       AS cout_main_oeuvre,
            COALESCE(SUM(COALESCE(ms.quantity_used, ms.quantity_planned) * ms.unit_cost), 0)     AS cout_pieces,
            COALESCE(SUM(COALESCE(ml.hours_actual, ml.hours_planned) * ml.hourly_rate), 0)
            + COALESCE(SUM(COALESCE(ms.quantity_used, ms.quantity_planned) * ms.unit_cost), 0)   AS cout_total,
            COUNT(DISTINCT o.id)                                                                 AS nombre_ot
        FROM ordre_travail o
        LEFT JOIN maintenance_labor      ml ON ml.maintenance_order_id = o.id
        LEFT JOIN maintenance_spare_part ms ON ms.maintenance_order_id = o.id
        WHERE o.statut = 'COMPLETED'
        AND (:entityType IS NULL OR o.entity_type = :entityType)
        AND EXTRACT(YEAR FROM COALESCE(o.completed_at, o.updated_at)) BETWEEN :debut AND :fin
        GROUP BY EXTRACT(YEAR FROM COALESCE(o.completed_at, o.updated_at))
        ORDER BY EXTRACT(YEAR FROM COALESCE(o.completed_at, o.updated_at))
        """, nativeQuery = true)
    List<Object[]> aggregateParAnneeNative(
            @Param("entityType") String entityType,
            @Param("debut") int debut,
            @Param("fin") int fin);

    // Détail OT terminés
    @Query("""
            SELECT o FROM OrdreTravail o
            WHERE o.statut = 'COMPLETED'
            AND (:entityType IS NULL OR o.entityType = :entityType)
            AND COALESCE(o.completedAt, o.updatedAt) BETWEEN :debut AND :fin
            ORDER BY COALESCE(o.completedAt, o.updatedAt) DESC
            """)
    List<OrdreTravail> findDetailPourRapport(
            @Param("entityType") OrdreTravail.TypeEntite entityType,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Synthèse globale – main d'œuvre (LEFT JOIN maintenance_labor)
    @Query(value = """
            SELECT COALESCE(SUM(COALESCE(ml.hours_actual, ml.hours_planned) * ml.hourly_rate), 0)
            FROM ordre_travail o
            LEFT JOIN maintenance_labor ml ON ml.maintenance_order_id = o.id
            WHERE o.statut = 'COMPLETED'
            AND (:entityType IS NULL OR o.entity_type = :entityType)
            AND COALESCE(o.completed_at, o.updated_at) BETWEEN :debut AND :fin
            """, nativeQuery = true)
    java.math.BigDecimal sumMainOeuvrePourRapport(
            @Param("entityType") String entityType,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // Synthèse globale – pièces (LEFT JOIN maintenance_spare_part)
    @Query(value = """
            SELECT COALESCE(SUM(COALESCE(ms.quantity_used, ms.quantity_planned) * ms.unit_cost), 0)
            FROM ordre_travail o
            LEFT JOIN maintenance_spare_part ms ON ms.maintenance_order_id = o.id
            WHERE o.statut = 'COMPLETED'
            AND (:entityType IS NULL OR o.entity_type = :entityType)
            AND COALESCE(o.completed_at, o.updated_at) BETWEEN :debut AND :fin
            """, nativeQuery = true)
    java.math.BigDecimal sumPiecesPourRapport(
            @Param("entityType") String entityType,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── Changement de lames ────────────────────────────────────────────────────

    /**
     * Historique de tous les OTs de type CHANGEMENT_LAMES COMPLETED
     * pour une machine donnée, du plus récent au plus ancien.
     */
    @Query("""
            SELECT o FROM OrdreTravail o
            WHERE o.entityType = 'MACHINE'
            AND o.entityId = :machineId
            AND o.typeMaintenance = 'CHANGEMENT_LAMES'
            AND o.statut = 'COMPLETED'
            ORDER BY o.completedAt DESC
            """)
    List<OrdreTravail> findChangementsLamesByMachine(@Param("machineId") Long machineId);

    /**
     * Dernier changement de lames COMPLETED pour une machine donnée.
     */
    @Query("""
            SELECT o FROM OrdreTravail o
            WHERE o.entityType = 'MACHINE'
            AND o.entityId = :machineId
            AND o.typeMaintenance = 'CHANGEMENT_LAMES'
            AND o.statut = 'COMPLETED'
            ORDER BY o.completedAt DESC
            LIMIT 1
            """)
    Optional<OrdreTravail> findDernierChangementLames(@Param("machineId") Long machineId);

    @Query(value = """
    SELECT o.* FROM ordre_travail o
    WHERE (:statut IS NULL OR o.statut = :statut)
    AND (:entityType IS NULL OR o.entity_type = :entityType)
    AND (:search IS NULL
         OR LOWER(o.reference) LIKE :search
         OR LOWER(o.description) LIKE :search)
    AND (CAST(:dateDebut AS timestamp) IS NULL OR o.created_at >= CAST(:dateDebut AS timestamp))
    AND (CAST(:dateFin AS timestamp) IS NULL OR o.created_at <= CAST(:dateFin AS timestamp))
    """,
            countQuery = """
    SELECT count(*) FROM ordre_travail o
    WHERE (:statut IS NULL OR o.statut = :statut)
    AND (:entityType IS NULL OR o.entity_type = :entityType)
    AND (:search IS NULL
         OR LOWER(o.reference) LIKE :search
         OR LOWER(o.description) LIKE :search)
    AND (CAST(:dateDebut AS timestamp) IS NULL OR o.created_at >= CAST(:dateDebut AS timestamp))
    AND (CAST(:dateFin AS timestamp) IS NULL OR o.created_at <= CAST(:dateFin AS timestamp))
    """,
            nativeQuery = true)
    Page<OrdreTravail> findAllFiltered(
            @Param("statut") String statut,
            @Param("entityType") String entityType,
            @Param("search") String search,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
            FROM OrdreTravail o
            WHERE o.statut = 'COMPLETED'
            """)
    java.math.BigDecimal sumAllCout();

    @Query("""
        SELECT EXTRACT(YEAR FROM COALESCE(o.completedAt, o.updatedAt)), EXTRACT(MONTH FROM COALESCE(o.completedAt, o.updatedAt)), COALESCE(SUM(o.actualLaborCost + o.actualPartsCost), 0)
        FROM OrdreTravail o
        WHERE o.statut = 'COMPLETED'
        AND COALESCE(o.completedAt, o.updatedAt) >= :fromDate
        GROUP BY EXTRACT(YEAR FROM COALESCE(o.completedAt, o.updatedAt)), EXTRACT(MONTH FROM COALESCE(o.completedAt, o.updatedAt))
        ORDER BY EXTRACT(YEAR FROM COALESCE(o.completedAt, o.updatedAt)), EXTRACT(MONTH FROM COALESCE(o.completedAt, o.updatedAt))
        """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") LocalDateTime fromDate);

}
