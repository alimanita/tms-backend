package com.transport.tms.dto.fleet.response;


import com.transport.tms.domain.entity.fleet.PlanMaintenance;
import com.transport.tms.domain.enums.TypeMaintenance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record PlanMaintenanceResponse(
    Long id,
    PlanMaintenance.TypeEntite entityType,
    Long entityId,
    String entityRef,
    TypeMaintenance typeMaintenance,
    String typeMaintenanceLabel,
    PlanMaintenance.TypeDeclencheur triggerType,
    BigDecimal triggerValue,
    String triggerUnit,
    LocalDate lastPerformedDate,
    BigDecimal lastPerformedKm,
    BigDecimal lastPerformedHours,
    LocalDate nextDueDate,
    BigDecimal nextDueKm,
    BigDecimal nextDueHours,
    BigDecimal alertThreshold,
    Boolean isActive,
    LocalDateTime createdAt
) {}