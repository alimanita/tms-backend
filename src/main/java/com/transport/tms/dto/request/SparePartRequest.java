package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SparePartRequest(
        @NotBlank String reference,
        @NotBlank String designation,
        String category,
        String supplier,
        BigDecimal purchasePrice,
        @NotNull BigDecimal stockQty,
        @NotNull BigDecimal minThreshold
) {}
