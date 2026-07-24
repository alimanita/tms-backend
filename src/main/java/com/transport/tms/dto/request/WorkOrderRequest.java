package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WorkOrderRequest(

        @NotNull WorkOrderEntityType entityType,
        @NotNull Long entityId,
         WorkOrderType orderType,
        WorkOrderPriority priority,
        String maintenanceType,
        String description,
        LocalDate scheduledDate,
        BigDecimal mileageAtOrder,
        BigDecimal hoursAtOrder,
        BigDecimal estimatedCost,
        BigDecimal actualCost,
        String notes
) {}
