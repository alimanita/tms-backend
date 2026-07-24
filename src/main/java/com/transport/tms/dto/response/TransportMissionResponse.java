package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.MissionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransportMissionResponse(
        Long id,
        String reference,
        Long customerOrderId,
        String customerOrderReference,
        Long customerId,
        String customerName,
        Long vehicleId,
        String vehicleRegistration,
        Long driverId,
        String driverName,
        Instant departureDate,
        Instant expectedArrival,
        Instant actualArrival,
        String loadingAddress,
        String deliveryAddress,
        MissionStatus status,
        BigDecimal revenue,
        BigDecimal transportCost,
        BigDecimal totalExpenses,
        String notes,
        String cancellationReason
) {}
