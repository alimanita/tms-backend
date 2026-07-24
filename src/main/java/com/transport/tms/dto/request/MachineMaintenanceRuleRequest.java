package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MachineMaintenanceRuleRequest(
        @NotBlank String code,
        String description,
        String actionType,
        Integer intervalHours,
        Integer intervalDays,
        String consumable,
        BigDecimal quantity,
        String quantityUnit,
        BigDecimal lastPerformedHours,
        LocalDate lastPerformedDate,
        Boolean active
) {}
