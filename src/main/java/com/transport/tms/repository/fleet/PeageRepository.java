package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.Peage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeageRepository extends JpaRepository<Peage, Long>, JpaSpecificationExecutor<Peage> {
    List<Peage> findByVehiculeId(Long vehiculeId);
    List<Peage> findByChauffeurId(Long chauffeurId);
    Page<Peage> findByChauffeurId(Long chauffeurId, Pageable pageable);
    List<Peage> findByMissionId(Long missionId);
    Page<Peage> findAll(Pageable pageable);

    // ── 1. Unicité par numéro de reçu (contrôle primaire) ──────────────────────
    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.receiptNumber IS NOT NULL
        AND (
            p.receiptNumber = :receiptNumber
            OR REPLACE(REPLACE(REPLACE(UPPER(p.receiptNumber), ' ', ''), '-', ''), '_', '') = :normalizedReceipt
        )
    """)
    boolean existsByReceiptNumber(
            @org.springframework.data.repository.query.Param("receiptNumber") String receiptNumber,
            @org.springframework.data.repository.query.Param("normalizedReceipt") String normalizedReceipt);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.id <> :id
        AND p.receiptNumber IS NOT NULL
        AND (
            p.receiptNumber = :receiptNumber
            OR REPLACE(REPLACE(REPLACE(UPPER(p.receiptNumber), ' ', ''), '-', ''), '_', '') = :normalizedReceipt
        )
    """)
    boolean existsByReceiptNumberAndIdNot(
            @org.springframework.data.repository.query.Param("receiptNumber") String receiptNumber,
            @org.springframework.data.repository.query.Param("normalizedReceipt") String normalizedReceipt,
            @org.springframework.data.repository.query.Param("id") Long id);

    // ── Fallback : même véhicule + même jour + même montant TTC ─────────────────
    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.vehicule.id = :vehiculeId
        AND CAST(p.datePassage AS LocalDate) = CAST(:datePassage AS LocalDate)
        AND (
            p.amountTTC = :amountTTC
            OR ABS(p.amountTTC - :amountTTC) < 0.1
        )
    """)
    boolean existsByVehiculeAndDateAndAmount(
            @org.springframework.data.repository.query.Param("vehiculeId") Long vehiculeId,
            @org.springframework.data.repository.query.Param("datePassage") java.time.LocalDateTime datePassage,
            @org.springframework.data.repository.query.Param("amountTTC") java.math.BigDecimal amountTTC);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.vehicule.id = :vehiculeId
        AND CAST(p.datePassage AS LocalDate) = CAST(:datePassage AS LocalDate)
        AND (
            p.amountTTC = :amountTTC
            OR ABS(p.amountTTC - :amountTTC) < 0.1
        )
        AND p.id <> :id
    """)
    boolean existsByVehiculeAndDateAndAmountAndIdNot(
            @org.springframework.data.repository.query.Param("vehiculeId") Long vehiculeId,
            @org.springframework.data.repository.query.Param("datePassage") java.time.LocalDateTime datePassage,
            @org.springframework.data.repository.query.Param("amountTTC") java.math.BigDecimal amountTTC,
            @org.springframework.data.repository.query.Param("id") Long id);

    // ── Doublon par date+heure exacte + même gare entrée + même gare sortie ───
    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.datePassage = :datePassage
        AND (
            (:gareEntree IS NULL AND p.gareEntree IS NULL)
            OR UPPER(TRIM(p.gareEntree)) = UPPER(TRIM(:gareEntree))
        )
        AND (
            (:gareSortie IS NULL AND p.gareSortie IS NULL)
            OR UPPER(TRIM(p.gareSortie)) = UPPER(TRIM(:gareSortie))
        )
    """)
    boolean existsByDateHeureAndGares(
            @org.springframework.data.repository.query.Param("datePassage") java.time.LocalDateTime datePassage,
            @org.springframework.data.repository.query.Param("gareEntree") String gareEntree,
            @org.springframework.data.repository.query.Param("gareSortie") String gareSortie);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COUNT(p) > 0 FROM Peage p
        WHERE p.datePassage = :datePassage
        AND (
            (:gareEntree IS NULL AND p.gareEntree IS NULL)
            OR UPPER(TRIM(p.gareEntree)) = UPPER(TRIM(:gareEntree))
        )
        AND (
            (:gareSortie IS NULL AND p.gareSortie IS NULL)
            OR UPPER(TRIM(p.gareSortie)) = UPPER(TRIM(:gareSortie))
        )
        AND p.id <> :id
    """)
    boolean existsByDateHeureAndGaresAndIdNot(
            @org.springframework.data.repository.query.Param("datePassage") java.time.LocalDateTime datePassage,
            @org.springframework.data.repository.query.Param("gareEntree") String gareEntree,
            @org.springframework.data.repository.query.Param("gareSortie") String gareSortie,
            @org.springframework.data.repository.query.Param("id") Long id);


    // ── Filtrage paginé ─────────────────────────────────────────────────────────
    @org.springframework.data.jpa.repository.Query("""
        SELECT p FROM Peage p
        WHERE (:vehiculeId IS NULL OR p.vehicule.id = :vehiculeId)
        AND (:chauffeurId IS NULL OR p.chauffeur.id = :chauffeurId)
        AND (:startDate IS NULL OR p.datePassage >= :startDate)
        AND (:endDate IS NULL OR p.datePassage <= :endDate)
    """)
    Page<Peage> findAllFiltered(
            @org.springframework.data.repository.query.Param("vehiculeId") Long vehiculeId,
            @org.springframework.data.repository.query.Param("chauffeurId") Long chauffeurId,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT p FROM Peage p
        LEFT JOIN FETCH p.vehicule v
        LEFT JOIN FETCH p.chauffeur c
        WHERE p.datePassage BETWEEN :debut AND :fin
        AND (:filterVehicule = false OR v.id IN :vehiculeIds)
        AND (:filterChauffeur = false OR c.id IN :chauffeurIds OR (c.id IS NULL AND v.id IN (SELECT m.vehicule.id FROM Mission m JOIN m.chauffeurSlots cs WHERE m.statut = com.transport.tms.domain.entity.fleet.Mission.StatutMission.COMPLETED AND cs.chauffeur.id IN :chauffeurIds)))
    """)
    List<Peage> findStandaloneForBilanExploitation(
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDateTime debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin,
            @org.springframework.data.repository.query.Param("vehiculeIds") List<Long> vehiculeIds,
            @org.springframework.data.repository.query.Param("filterVehicule") boolean filterVehicule,
            @org.springframework.data.repository.query.Param("chauffeurIds") List<Long> chauffeurIds,
            @org.springframework.data.repository.query.Param("filterChauffeur") boolean filterChauffeur);

    @org.springframework.data.jpa.repository.Query("""
            SELECT COALESCE(SUM(p.amountTTC), 0)
            FROM Peage p
            """)
    java.math.BigDecimal sumAllCoutPeage();

    @org.springframework.data.jpa.repository.Query("""
        SELECT EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage), COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.datePassage >= :fromDate
        GROUP BY EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage)
        ORDER BY EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage)
        """)
    List<Object[]> sumCostByYearMonth(@org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.mission.id = :missionId
    """)
    java.math.BigDecimal sumPeageByMissionId(@org.springframework.data.repository.query.Param("missionId") Long missionId);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.datePassage BETWEEN :debut AND :fin
    """)
    java.math.BigDecimal sumAllByPeriod(
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDateTime debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.chauffeur.id = :chauffeurId
        AND p.datePassage BETWEEN :debut AND :fin
    """)
    java.math.BigDecimal sumByChauffeurAndPeriod(
            @org.springframework.data.repository.query.Param("chauffeurId") Long chauffeurId,
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDateTime debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin);
}
