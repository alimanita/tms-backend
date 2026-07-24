package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OilChangeRequest(
        @NotNull Long vehicleId,
        @NotBlank String oilType,
        @NotNull LocalDate changeDate,
        @NotNull BigDecimal mileageAtChange,
        @NotNull BigDecimal quantityLiters,
        BigDecimal unitCost,
        BigDecimal totalCost,
        BigDecimal nextChangeKm,
        LocalDate nextChangeDate,
        String performedBy,
        String notes
) {}
