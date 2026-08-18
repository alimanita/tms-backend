package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PieceRechangeRequest(


    @Size(max = 50)
    String reference,

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 200)
    String name,

    @Size(max = 100)
    String brand,

    String unit,

    @DecimalMin(value = "0.0")
    BigDecimal unitCost,

    @DecimalMin(value = "0.0")
    BigDecimal stockQty,

    @DecimalMin(value = "0.0")
    BigDecimal minStockQty,

    Long stockItemId,

    String location,

    BigDecimal amountHT,
    BigDecimal tvaRate,
    BigDecimal tvaAmount,
    Boolean isTvaRecoverable,
    BigDecimal recoverableTvaAmount
) {}