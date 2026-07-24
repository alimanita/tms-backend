package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.MaintenanceTriggerType;
import com.transport.tms.domain.enums.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MaintenancePlanRequest(
        @NotNull Long vehicleId,
        @NotNull MaintenanceType maintenanceType,
        @NotNull MaintenanceTriggerType triggerType,
        @NotNull BigDecimal triggerValue,
        LocalDate lastPerformedDate,
        BigDecimal lastPerformedKm,
        LocalDate nextDueDate,
        BigDecimal nextDueKm,
        BigDecimal alertThreshold,
        boolean active
) {}
