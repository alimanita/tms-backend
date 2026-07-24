package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.MachineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MachineRequest(
        @NotBlank String reference,
        String serialNumber,
        @NotBlank String name,
        String brand,
        String model,
        String category,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String powerUnit,
        BigDecimal powerValue,
        @NotNull BigDecimal initialHours,
        @NotNull BigDecimal currentHours,
        String location,
        @NotNull MachineStatus status,
        String notes
) {}
