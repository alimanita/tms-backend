package com.transport.tms.dto.fleet.request;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateHeuresRequest(

        @NotNull(message = "Les heures sont obligatoires")
        @DecimalMin(value = "0", message = "Les heures ne peuvent pas être négatives")
        BigDecimal heuresActuelles

) {}