package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.MissionStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportMissionRequest(
        String reference,                  // plus de @NotBlank : optionnelle, auto-générée si absente
        Long customerOrderId,
        Long customerId,
        @NotNull Long vehicleId,
        @NotNull Long driverId,
        @NotNull Instant departureDate,
        Instant expectedArrival,
        String loadingAddress,
        String deliveryAddress,
        @NotNull MissionStatus status,
        BigDecimal revenue,
        BigDecimal transportCost,
        String notes
) {}