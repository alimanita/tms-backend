package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OTPieceRechangeRequest(

    @NotNull(message = "La pièce de rechange est obligatoire")
    Long pieceRechangeId,

    @NotNull(message = "La quantité planifiée est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être > 0")
    BigDecimal quantityPlanned,

    BigDecimal quantityUsed,

    @DecimalMin(value = "0.00")
    BigDecimal unitCost
) {}