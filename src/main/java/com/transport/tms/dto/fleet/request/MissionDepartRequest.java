package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MissionDepartRequest(

    @NotNull(message = "Le kilométrage de départ est obligatoire")
    @DecimalMin(value = "0.0")
    BigDecimal mileageAtDeparture
) {}