package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record FuelRecordResponse(
        Long id,
        Long vehicleId,
        String vehicleRegistration,
        Long driverId,
        String driverName,
        Instant fillDate,
        BigDecimal mileage,
        String station,
        BigDecimal liters,
        BigDecimal pricePerLiter,
        BigDecimal totalAmount
) {}
