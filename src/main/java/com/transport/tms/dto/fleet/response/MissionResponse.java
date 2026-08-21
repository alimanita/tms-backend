package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.entity.fleet.Mission;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public record MissionResponse(
    Long id,
    String reference,
    String title,
    Long clientId,
    Long vehiculeId,
    String vehiculeRef,
    String vehiculeImmatriculation,
    java.util.List<Long> chauffeurIds,
    String chauffeursNoms,
    Mission.StatutMission statut,
    String departureLocation,
    String arrivalLocation,
    LocalDateTime plannedDeparture,
    LocalDateTime plannedReturn,
    LocalDateTime actualDeparture,
    LocalDateTime actualReturn,
    String purpose,
    String cargoDescription,
    BigDecimal cargoWeight,
    BigDecimal mileageAtDeparture,
    BigDecimal mileageAtReturn,
    BigDecimal totalKm,
    BigDecimal fuelCost,
    BigDecimal tollCost,
    BigDecimal otherExpenses,
    BigDecimal totalCost,
    BigDecimal revenue,
    Long invoiceId,
    String notes,
    Long approvedBy,
    LocalDateTime approvedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String letterMissionUrl
) {}