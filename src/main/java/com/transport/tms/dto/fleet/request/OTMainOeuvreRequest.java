package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record OTMainOeuvreRequest(

    @NotBlank(message = "Le nom du technicien est obligatoire")
    String technicianName,

    Boolean isExternal,

    @DecimalMin(value = "0.0")
    BigDecimal hoursPlanned,

    @DecimalMin(value = "0.0")
    BigDecimal hoursActual,

    @DecimalMin(value = "0.0")
    BigDecimal hourlyRate
) {}