package com.transport.tms.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
public record PieceRechangeRequest(
        @Size(max = 50) String reference,
        @NotBlank(message = "Le nom est obligatoire") @Size(max = 200) String name,
        @Size(max = 100) String category,
        @Size(max = 100) String supplier,
        @DecimalMin(value = "0.0") BigDecimal unitCost,
        @DecimalMin(value = "0.0") BigDecimal stockQty,
        @DecimalMin(value = "0.0") BigDecimal minStockQty
) {}