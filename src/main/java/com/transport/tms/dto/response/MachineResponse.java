package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.MachineStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record MachineResponse(
        Long id,
        String reference,
        String serialNumber,
        String name,
        String brand,
        String model,
        String category,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String powerUnit,
        BigDecimal powerValue,
        BigDecimal initialHours,
        BigDecimal currentHours,
        String location,
        MachineStatus status,
        String notes,
        boolean active,
        Instant createdAt
) {}
