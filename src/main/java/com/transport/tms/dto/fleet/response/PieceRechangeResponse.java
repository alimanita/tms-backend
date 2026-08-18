package com.transport.tms.dto.fleet.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
public record PieceRechangeResponse(
    Long id,
    String reference,
    String name,
    String brand,
    String unit,
    BigDecimal unitCost,
    BigDecimal stockQty,
    BigDecimal minStockQty,
    Boolean isLowStock,
    String location,
    Boolean isActive,
    LocalDateTime createdAt,
    BigDecimal amountHT,
    BigDecimal tvaRate,
    BigDecimal tvaAmount,
    Boolean isTvaRecoverable,
    BigDecimal recoverableTvaAmount,
    String receiptPath
) {}