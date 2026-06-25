package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.VehicleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VehicleResponse(
        Long id,
        String registration,
        String vin,
        String brand,
        String model,
        Integer year,
        String vehicleType,
        BigDecimal payloadKg,
        BigDecimal currentMileage,
        LocalDate acquisitionDate,
        LocalDate insuranceExpiry,
        VehicleStatus status,
        boolean active
) {}
