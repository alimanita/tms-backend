package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.PlanMaintenance;
import com.transport.tms.domain.enums.TypeMaintenance;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDate;

public record PlanMaintenanceRequest(

    @NotNull
    PlanMaintenance.TypeEntite entityType,

    @NotNull
    Long entityId,

    @NotNull(message = "Le type de maintenance est obligatoire")
    TypeMaintenance typeMaintenance,

    @NotNull
    PlanMaintenance.TypeDeclencheur triggerType,

    @DecimalMin(value = "0.0")
    BigDecimal triggerValue,

    String triggerUnit,

    LocalDate lastPerformedDate,

    BigDecimal lastPerformedKm,

    BigDecimal lastPerformedHours,

    LocalDate nextDueDate,

    BigDecimal nextDueKm,

    BigDecimal nextDueHours,

    BigDecimal alertThreshold
) {}