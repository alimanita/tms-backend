package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.DepenseMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DepenseMissionRepository extends JpaRepository<DepenseMission, Long> {

    List<DepenseMission> findByMissionId(Long missionId);

    List<DepenseMission> findByMissionIdAndExpenseType(
            Long missionId, DepenseMission.TypeDepense expenseType);

    // Total dépenses d'une mission
    @Query("""
            SELECT COALESCE(SUM(d.montant), 0)
            FROM DepenseMission d
            WHERE d.mission.id = :missionId
            """)
    BigDecimal sumByMission(@Param("missionId") Long missionId);

    // Total par type de dépense
    @Query("""
            SELECT COALESCE(SUM(d.montant), 0)
            FROM DepenseMission d
            WHERE d.mission.id = :missionId
            AND d.expenseType = :type
            """)
    BigDecimal sumByMissionAndType(
            @Param("missionId") Long missionId,
            @Param("type") DepenseMission.TypeDepense type);

    // ── RAPPORTS AMAZON : dépenses mission agrégées par mois ──────────────────
    @Query("""
            SELECT EXTRACT(YEAR FROM d.expenseDate), EXTRACT(MONTH FROM d.expenseDate),
                   COALESCE(SUM(d.montant), 0)
            FROM DepenseMission d
            WHERE d.expenseDate >= :fromDate
            GROUP BY EXTRACT(YEAR FROM d.expenseDate), EXTRACT(MONTH FROM d.expenseDate)
            ORDER BY EXTRACT(YEAR FROM d.expenseDate), EXTRACT(MONTH FROM d.expenseDate)
            """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") LocalDateTime fromDate);

    // ── RAPPORTS AMAZON : dépenses mission agrégées par type (catégorie) ──────
    @Query("""
            SELECT d.expenseType, COALESCE(SUM(d.montant), 0)
            FROM DepenseMission d
            GROUP BY d.expenseType
            """)
    List<Object[]> sumCostByExpenseType();
}