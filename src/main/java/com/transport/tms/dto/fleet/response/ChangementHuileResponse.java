package com.transport.tms.dto.fleet.response;


import com.transport.tms.domain.entity.fleet.ChangementHuile;
import com.transport.tms.domain.enums.TypeHuile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record ChangementHuileResponse(
    Long id,
    String reference,
    ChangementHuile.TypeEntite entityType,
    Long entityId,
    String entityRef,
    TypeHuile typeHuile,
    String typeHuileLabel,
    LocalDate changeDate,
    BigDecimal mileageAtChange,
    BigDecimal hoursAtChange,
    BigDecimal quantityLiters,
    BigDecimal unitCost,
    BigDecimal totalCost,
    BigDecimal nextChangeKm,
    BigDecimal nextChangeHours,
    LocalDate nextChangeDate,
    String performedBy,
    String notes,
    LocalDateTime createdAt
) {}