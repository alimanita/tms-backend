package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.DepenseMission;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepenseMissionRequest(

    @NotNull(message = "Le type de dépense est obligatoire")
    DepenseMission.TypeDepense expenseType,

    @NotNull
    @DecimalMin(value = "0.01", message = "Le montant doit être > 0")
    BigDecimal montant,

    String currency,

    @NotNull(message = "La date de la dépense est obligatoire")
    LocalDateTime expenseDate,

    @Size(max = 300)
    String description,

    String receiptPath,

    Boolean isReimbursable,

    // Nouveaux champs pour le PleinCarburant (optionnels)
    BigDecimal quantityLiters,
    BigDecimal pricePerLiter,
    BigDecimal mileageBefore,
    BigDecimal mileageAfter,
    Boolean isFullTank,
    String receiptNumber
) {}