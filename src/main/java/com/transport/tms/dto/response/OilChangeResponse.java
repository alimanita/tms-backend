package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OilChangeResponse(
        Long id,
        Long vehicleId,
        String vehicleRegistration,
        String oilType,
        LocalDate changeDate,
        BigDecimal mileageAtChange,
        BigDecimal quantityLiters,
        BigDecimal unitCost,
        BigDecimal totalCost,
        BigDecimal nextChangeKm,
        LocalDate nextChangeDate,
        String performedBy,
        String notes,
        Instant createdAt
) {}
