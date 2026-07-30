package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.AffectationPneu;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDate;

public record AffectationPneuRequest(

    @NotNull(message = "Le pneu est obligatoire")
    Long pneuId,

    @NotNull(message = "Le véhicule est obligatoire")
    Long vehiculeId,

    @NotNull(message = "La position est obligatoire")
    AffectationPneu.PositionPneu position,

    @NotNull(message = "La date de montage est obligatoire")
    LocalDate mountDate,

    @NotNull(message = "Le kilométrage au montage est obligatoire")
    @DecimalMin(value = "0.0")
    BigDecimal mountMileage,

    LocalDate unmountDate,

    BigDecimal unmountMileage,

    AffectationPneu.RaisonDemontage reasonUnmount,

    String notes
) {}