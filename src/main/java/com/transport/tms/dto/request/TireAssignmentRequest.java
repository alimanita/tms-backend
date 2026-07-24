package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.TirePosition;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TireAssignmentRequest(
        @NotNull Long tireId,
        @NotNull Long vehicleId,
        @NotNull TirePosition position,
        @NotNull LocalDate mountDate,
        @NotNull BigDecimal mountMileage,
        LocalDate unmountDate,
        BigDecimal unmountMileage,
        String reasonUnmount,
        String notes
) {}
