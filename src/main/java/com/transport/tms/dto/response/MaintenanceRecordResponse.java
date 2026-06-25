package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MaintenanceRecordResponse(
        Long id,
        Long vehicleId,
        String vehicleRegistration,
        String maintenanceType,
        LocalDate maintenanceDate,
        BigDecimal mileage,
        BigDecimal cost,
        String supplier,
        LocalDate nextDueDate,
        BigDecimal nextDueMileage
) {}
