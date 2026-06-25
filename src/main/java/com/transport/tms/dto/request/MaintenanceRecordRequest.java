package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MaintenanceRecordRequest(
        @NotNull Long vehicleId,
        @NotBlank String maintenanceType,
        @NotNull LocalDate maintenanceDate,
        BigDecimal mileage,
        @NotNull BigDecimal cost,
        String supplier,
        LocalDate nextDueDate,
        BigDecimal nextDueMileage
) {}
