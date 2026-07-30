package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;

public record OTPieceRechangeResponse(
    Long id,
    Long pieceRechangeId,
    String pieceReference,
    String pieceName,
    BigDecimal quantityPlanned,
    BigDecimal quantityUsed,
    BigDecimal unitCost,
    BigDecimal totalCost
) {}