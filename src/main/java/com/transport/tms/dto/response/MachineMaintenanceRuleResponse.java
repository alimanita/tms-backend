package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record MachineMaintenanceRuleResponse(
        Long id,
        Long machineId,
        String machineReference,
        String code,
        String description,
        String actionType,
        Integer intervalHours,
        Integer intervalDays,
        String consumable,
        BigDecimal quantity,
        String quantityUnit,
        BigDecimal lastPerformedHours,
        LocalDate lastPerformedDate,
        boolean active,
        Instant createdAt
) {}
