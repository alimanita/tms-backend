package com.transport.tms.repository;

import com.transport.tms.domain.entity.MissionExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface MissionExpenseRepository extends JpaRepository<MissionExpense, Long> {
    List<MissionExpense> findAllByMission_IdOrderByExpenseDateDesc(Long missionId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM MissionExpense e WHERE e.mission.id = :missionId")
    BigDecimal sumAmountByMissionId(@Param("missionId") Long missionId);
}
