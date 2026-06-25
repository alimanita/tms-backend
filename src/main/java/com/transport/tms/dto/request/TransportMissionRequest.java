package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.MissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportMissionRequest(
        @NotBlank String reference,
        Long customerOrderId,
        Long customerId,
        Long vehicleId,
        Long driverId,
        Instant departureDate,
        Instant expectedArrival,
        String loadingAddress,
        String deliveryAddress,
        @NotNull MissionStatus status,
        BigDecimal revenue,
        BigDecimal transportCost,
        String notes
) {}
