package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.Pneu;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDate;

public record PneuRequest(

    @Size(max = 100)
    String serialNumber,

    @Size(max = 100)
    String brand,

    @Size(max = 100)
    String model,

    @Size(max = 50)
    String size,

    Pneu.TypePneu type,

    LocalDate purchaseDate,

    @DecimalMin(value = "0.0")
    BigDecimal purchaseCost,

    @DecimalMin(value = "0.0")
    BigDecimal maxKm
) {}