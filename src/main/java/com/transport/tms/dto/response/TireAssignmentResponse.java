package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.TirePosition;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TireAssignmentResponse(
        Long id,
        Long tireId,
        String tireSerialNumber,
        Long vehicleId,
        String vehicleRegistration,
        TirePosition position,
        LocalDate mountDate,
        BigDecimal mountMileage,
        LocalDate unmountDate,
        BigDecimal unmountMileage,
        String reasonUnmount,
        String notes,
        Instant createdAt
) {}
