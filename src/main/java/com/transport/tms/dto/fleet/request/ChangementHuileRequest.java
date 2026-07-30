package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.ChangementHuile;
import com.transport.tms.domain.enums.TypeHuile;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDate;

public record ChangementHuileRequest(

    @NotNull
    ChangementHuile.TypeEntite entityType,

    @NotNull
    Long entityId,

    @NotNull(message = "Le type d'huile est obligatoire")
    TypeHuile typeHuile,

    @NotNull(message = "La date est obligatoire")
    LocalDate changeDate,

    BigDecimal mileageAtChange,

    BigDecimal hoursAtChange,

    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal quantityLiters,

    BigDecimal unitCost,

    BigDecimal totalCost,

    BigDecimal nextChangeKm,

    BigDecimal nextChangeHours,

    LocalDate nextChangeDate,

    String performedBy,

    String notes
) {}