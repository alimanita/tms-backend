package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.PlanMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanMaintenanceRepository extends JpaRepository<PlanMaintenance, Long> {

    List<PlanMaintenance> findByEntityTypeAndEntityId(
            PlanMaintenance.TypeEntite entityType, Long entityId);

    List<PlanMaintenance> findByEntityTypeAndEntityIdAndIsActiveTrue(
            PlanMaintenance.TypeEntite entityType, Long entityId);

    // Plans dont la prochaine date est dépassée ou proche
    @Query("""
            SELECT p FROM PlanMaintenance p
            WHERE p.isActive = true
            AND p.triggerType = 'CALENDAR'
            AND p.nextDueDate <= :limit
            ORDER BY p.nextDueDate ASC
            """)
    List<PlanMaintenance> findEcheancesCalendrierProches(@Param("limit") LocalDate limit);

    // Plans dont le prochain km est atteint
    @Query("""
            SELECT p FROM PlanMaintenance p
            WHERE p.isActive = true
            AND p.triggerType = 'KM'
            AND p.nextDueKm <= :kmActuel
            """)
    List<PlanMaintenance> findEcheancesKmAtteintes(@Param("kmActuel") BigDecimal kmActuel);

    // Plans dont les heures sont atteintes
    @Query("""
            SELECT p FROM PlanMaintenance p
            WHERE p.isActive = true
            AND p.triggerType = 'HOURS'
            AND p.nextDueHours <= :heuresActuelles
            """)
    List<PlanMaintenance> findEcheancesHeuresAtteintes(
            @Param("heuresActuelles") BigDecimal heuresActuelles);

    // Tous les plans actifs avec alerte imminente
    @Query("""
            SELECT p FROM PlanMaintenance p
            WHERE p.isActive = true
            AND (
                (p.triggerType = 'CALENDAR' AND p.nextDueDate <= :limitDate)
                OR (p.triggerType = 'KM' AND p.nextDueKm - :kmActuel <= p.alertThreshold)
                OR (p.triggerType = 'HOURS' AND p.nextDueHours - :heuresActuelles <= p.alertThreshold)
            )
            """)
    List<PlanMaintenance> findTousPlansAvecAlerteImminente(
            @Param("limitDate") LocalDate limitDate,
            @Param("kmActuel") BigDecimal kmActuel,
            @Param("heuresActuelles") BigDecimal heuresActuelles);
}