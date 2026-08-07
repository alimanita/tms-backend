package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.PleinCarburant;
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
public interface PleinCarburantRepository extends JpaRepository<PleinCarburant, Long> {

    Optional<PleinCarburant> findByReference(String reference);

    boolean existsByReference(String reference);

    @Query("SELECT MAX(CAST(SUBSTRING(p.reference, 11) AS int)) FROM PleinCarburant p WHERE p.reference LIKE CONCAT(:prefix, '%')")
    Long findMaxSequenceForYear(@Param("prefix") String prefix);

    List<PleinCarburant> findByVehiculeIdOrderByFillingDateDesc(Long vehiculeId);

    Page<PleinCarburant> findByVehiculeId(Long vehiculeId, Pageable pageable);

    List<PleinCarburant> findByChauffeurIdOrderByFillingDateDesc(Long chauffeurId);

    Optional<PleinCarburant> findTopByVehiculeIdOrderByFillingDateDesc(Long vehiculeId);

    @Query("""
            SELECT COALESCE(SUM(p.quantityLiters * p.pricePerLiter), 0)
            FROM PleinCarburant p
            WHERE FUNCTION('YEAR', p.fillingDate) = :annee
            AND FUNCTION('MONTH', p.fillingDate) = :mois
            """)
    BigDecimal sumCoutCarburantMois(
            @Param("annee") int annee,
            @Param("mois") int mois);

    @Query("SELECT AVG(p.consumptionRate) FROM PleinCarburant p WHERE p.vehicule.id = :vehiculeId AND p.consumptionRate IS NOT NULL")
    Double avgConsommationByVehicule(@Param("vehiculeId") Long vehiculeId);

    List<PleinCarburant> findByVehiculeIdAndFillingDateBetweenOrderByFillingDateDesc(
            Long vehiculeId, LocalDateTime debut, LocalDateTime fin);

    // Stats pour le dashboard chauffeur
    long countByChauffeurIdAndFillingDateBetween(
            Long chauffeurId, LocalDateTime debut, LocalDateTime fin);

    @Query("""
            SELECT COALESCE(SUM(p.quantityLiters * p.pricePerLiter), 0)
            FROM PleinCarburant p
            WHERE p.chauffeur.id = :chauffeurId
            AND p.fillingDate BETWEEN :debut AND :fin
            """)
    BigDecimal sumCoutByChauffeurAndPeriode(
            @Param("chauffeurId") Long chauffeurId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── RAPPORTS CARBURANT : agrégation par mois (native) ─────────────────────
    @Query(value = """
    SELECT
        EXTRACT(YEAR FROM p.filling_date)::int AS annee,
        EXTRACT(MONTH FROM p.filling_date)::int AS mois,
        COALESCE(SUM(p.quantity_liters), 0) AS litres_totaux,
        COALESCE(SUM(p.quantity_liters * p.price_per_liter), 0) AS cout_total,
        COUNT(*) AS nombre_pleins,
        AVG(p.consumption_rate) AS consommation_moyenne
    FROM fuel_filling p
    WHERE p.filling_date BETWEEN :debut AND :fin
    AND (:vehiculeId IS NULL OR p.vehicle_id = :vehiculeId)
    GROUP BY EXTRACT(YEAR FROM p.filling_date), EXTRACT(MONTH FROM p.filling_date)
    ORDER BY EXTRACT(YEAR FROM p.filling_date), EXTRACT(MONTH FROM p.filling_date)
    """, nativeQuery = true)
    List<Object[]> aggregateParMoisNative(
            @Param("vehiculeId") Long vehiculeId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ── RAPPORTS CARBURANT : agrégation par année (native) ─────────────────────
    @Query(value = """
    SELECT
        EXTRACT(YEAR FROM p.filling_date)::int AS annee,
        COALESCE(SUM(p.quantity_liters), 0) AS litres_totaux,
        COALESCE(SUM(p.quantity_liters * p.price_per_liter), 0) AS cout_total,
        COUNT(*) AS nombre_pleins
    FROM fuel_filling p
    WHERE EXTRACT(YEAR FROM p.filling_date) BETWEEN :anDebut AND :anFin
    AND (:vehiculeId IS NULL OR p.vehicle_id = :vehiculeId)
    GROUP BY EXTRACT(YEAR FROM p.filling_date)
    ORDER BY EXTRACT(YEAR FROM p.filling_date)
    """, nativeQuery = true)
    List<Object[]> aggregateParAnneeNative(
            @Param("vehiculeId") Long vehiculeId,
            @Param("anDebut") int anDebut,
            @Param("anFin") int anFin);

    // ── RAPPORTS CARBURANT : détail des pleins ────────────────────────────────
    @Query("""
            SELECT p FROM PleinCarburant p
            LEFT JOIN FETCH p.vehicule
            LEFT JOIN FETCH p.chauffeur
            WHERE p.fillingDate BETWEEN :debut AND :fin
            AND (:vehiculeId IS NULL OR p.vehicule.id = :vehiculeId)
            ORDER BY p.fillingDate DESC
            """)
    List<PleinCarburant> findDetailPourRapport(
            @Param("vehiculeId") Long vehiculeId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT COALESCE(SUM(p.quantityLiters * p.pricePerLiter), 0)
            FROM PleinCarburant p
            """)
    BigDecimal sumAllCoutCarburant();

    @Query("""
        SELECT EXTRACT(YEAR FROM p.fillingDate), EXTRACT(MONTH FROM p.fillingDate), COALESCE(SUM(p.quantityLiters * p.pricePerLiter), 0)
        FROM PleinCarburant p
        WHERE p.fillingDate >= :fromDate
        GROUP BY EXTRACT(YEAR FROM p.fillingDate), EXTRACT(MONTH FROM p.fillingDate)
        ORDER BY EXTRACT(YEAR FROM p.fillingDate), EXTRACT(MONTH FROM p.fillingDate)
        """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") LocalDateTime fromDate);
}