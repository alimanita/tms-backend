package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record FuelRecordRequest(
        @NotNull Long vehicleId,
        Long driverId,
        @NotNull Instant fillDate,
        @NotNull BigDecimal mileage,
        String station,
        @NotNull BigDecimal liters,
        @NotNull BigDecimal pricePerLiter
) {}
