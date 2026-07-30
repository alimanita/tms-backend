package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.domain.enums.TypeMaintenance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrdreTravailResponse(
    Long id,
    String reference,
    OrdreTravail.TypeEntite entityType,
    Long entityId,
    String entityRef,              // ex: "VH-2024-001 — Mercedes Actros"
    TypeMaintenance typeMaintenance,
    String typeMaintenanceLabel,
    OrdreTravail.TypeOrdre typeOrdre,
    OrdreTravail.PrioriteOT priorite,
    OrdreTravail.StatutOT statut,
    String description,
    LocalDate reportedDate,
    LocalDate scheduledDate,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    BigDecimal mileageAtOrder,
    BigDecimal hoursAtOrder,
    String workshop,
    Boolean isExternal,
    String externalProvider,
    BigDecimal estimatedCost,
    BigDecimal actualLaborCost,
    BigDecimal actualPartsCost,
    BigDecimal actualTotalCost,
    BigDecimal downtimeHours,
    List<OTPieceRechangeResponse> pieces,
    List<OTMainOeuvreResponse> mainOeuvres,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}