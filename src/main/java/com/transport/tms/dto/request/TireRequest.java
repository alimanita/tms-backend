package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.TireStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TireRequest(
        @NotBlank String serialNumber,
        String brand,
        String model,
        String size,
        String type,
        LocalDate purchaseDate,
        BigDecimal purchaseCost,
        BigDecimal maxKm,
        @NotNull TireStatus status
) {}
