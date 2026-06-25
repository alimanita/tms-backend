package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VehicleRequest(
        @NotBlank String registration,
        String vin,
        String brand,
        String model,
        Integer year,
        String vehicleType,
        BigDecimal payloadKg,
        @NotNull BigDecimal currentMileage,
        LocalDate acquisitionDate,
        LocalDate insuranceExpiry,
        @NotNull VehicleStatus status
) {}
