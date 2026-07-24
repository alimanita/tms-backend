package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record WorkOrderResponse(
        Long id,
        String reference,
        WorkOrderEntityType entityType,
        Long entityId,
        String entityLabel,
        WorkOrderType orderType,
        WorkOrderPriority priority,
        WorkOrderStatus status,
        String maintenanceType,
        String description,
        LocalDate scheduledDate,
        Instant startedAt,
        Instant completedAt,
        BigDecimal mileageAtOrder,
        BigDecimal hoursAtOrder,
        BigDecimal estimatedCost,
        BigDecimal actualCost,
        String notes,
        Instant createdAt
) {}
