package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.domain.enums.TypeMaintenance;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDate;

public record OrdreTravailRequest(

    @NotNull(message = "Le type d'entité est obligatoire")
    OrdreTravail.TypeEntite entityType,

    @NotNull(message = "L'ID de l'entité est obligatoire")
    Long entityId,

    @NotNull(message = "Le type de maintenance est obligatoire")
    TypeMaintenance typeMaintenance,

    Long planMaintenanceId,

    @NotNull(message = "Le type d'ordre est obligatoire")
    OrdreTravail.TypeOrdre typeOrdre,

    OrdreTravail.PrioriteOT priorite,

    String description,

    Long reportedBy,

    LocalDate reportedDate,

    LocalDate scheduledDate,

    BigDecimal mileageAtOrder,

    BigDecimal hoursAtOrder,

    Long technicianId,

    String workshop,

    Boolean isExternal,

    String externalProvider,

    BigDecimal estimatedCost,

    String notes
) {}