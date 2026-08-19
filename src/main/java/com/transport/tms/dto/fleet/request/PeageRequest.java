package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PeageRequest(
        @NotNull(message = "Le véhicule est obligatoire")
        Long vehiculeId,

        Long chauffeurId,
        Long missionId,

        @NotNull(message = "La date du passage est obligatoire")
        LocalDateTime datePassage,

        BigDecimal amountHT,
        BigDecimal tvaRate,
        BigDecimal tvaAmount,

        @NotNull(message = "Le montant TTC est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être > 0")
        BigDecimal amountTTC,

        @Size(max = 150)
        String gareEntree,

        @Size(max = 150)
        String gareSortie,

        @Size(max = 100)
        String receiptNumber,

        @Size(max = 100)
        String societeAutoroute,

        @Size(max = 1000)
        String notes
) {}
