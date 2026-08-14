package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MissionRequest(

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200)
    String title,

    Long clientId,

    @NotNull(message = "Le véhicule est obligatoire")
    Long vehiculeId,

    @NotNull(message = "Le chauffeur est obligatoire")
    Long chauffeurId,

    @NotBlank(message = "Le lieu de départ est obligatoire")
    String departureLocation,

    @NotBlank(message = "Le lieu d'arrivée est obligatoire")
    String arrivalLocation,

    @NotNull(message = "La date de départ planifiée est obligatoire")

    LocalDateTime plannedDeparture,

    LocalDateTime plannedReturn,

    String purpose,

    String cargoDescription,

    @DecimalMin(value = "0.0")
    BigDecimal cargoWeight,

    String notes,

    @DecimalMin(value = "0.0")
    BigDecimal revenue
) {}