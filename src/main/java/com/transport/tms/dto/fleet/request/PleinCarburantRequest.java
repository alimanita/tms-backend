package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PleinCarburantRequest(

    @NotNull(message = "Le véhicule est obligatoire")
    Long vehiculeId,

    Long chauffeurId,

    @NotNull(message = "La date du plein est obligatoire")
    LocalDateTime fillingDate,

    @NotBlank(message = "Le type de carburant est obligatoire")
    String fuelType,

    @NotNull
    @DecimalMin(value = "0.01", message = "La quantité doit être > 0")
    BigDecimal quantityLiters,

    @NotNull
    @DecimalMin(value = "0.001", message = "Le prix/litre doit être > 0")
    BigDecimal pricePerLiter,

    BigDecimal mileageBefore,

    BigDecimal mileageAfter,

    Boolean isFullTank,

    String receiptNumber,

    String notes
) {}