package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ClotureRequest(
    @NotNull(message = "Le coût réel est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le coût réel doit être positif")
    BigDecimal coutReel
) {}