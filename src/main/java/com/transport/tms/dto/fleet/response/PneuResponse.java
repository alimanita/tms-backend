package com.transport.tms.dto.fleet.response;


import com.transport.tms.domain.entity.fleet.Pneu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PneuResponse(
    Long id,
    String serialNumber,
    String brand,
    String model,
    String size,
    Pneu.TypePneu type,
    LocalDate purchaseDate,
    BigDecimal purchaseCost,
    BigDecimal maxKm,
    Pneu.StatutPneu status,
    Boolean isActive,
    LocalDateTime createdAt
) {}