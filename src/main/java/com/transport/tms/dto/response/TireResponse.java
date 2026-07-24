package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.TireStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TireResponse(
        Long id,
        String serialNumber,
        String brand,
        String model,
        String size,
        String type,
        LocalDate purchaseDate,
        BigDecimal purchaseCost,
        BigDecimal maxKm,
        TireStatus status,
        boolean active,
        Instant createdAt
) {}
