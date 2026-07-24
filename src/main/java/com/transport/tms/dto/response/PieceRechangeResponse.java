package com.transport.tms.dto.response;

import java.math.BigDecimal;

import java.time.LocalDateTime;
public record PieceRechangeResponse(
        Long id,
        String reference,
        String name,
        String category,
        String supplier,
        BigDecimal unitCost,
        BigDecimal stockQty,
        BigDecimal minStockQty,
        Boolean isLowStock,
        Boolean isActive
) {}