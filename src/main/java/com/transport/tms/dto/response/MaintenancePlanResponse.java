package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.MaintenanceTriggerType;
import com.transport.tms.domain.enums.MaintenanceType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record MaintenancePlanResponse(
        Long id,
        Long vehicleId,
        String vehicleRegistration,
        MaintenanceType maintenanceType,
        MaintenanceTriggerType triggerType,
        BigDecimal triggerValue,
        LocalDate lastPerformedDate,
        BigDecimal lastPerformedKm,
        LocalDate nextDueDate,
        BigDecimal nextDueKm,
        BigDecimal alertThreshold,
        boolean active,
        Instant createdAt
) {}
