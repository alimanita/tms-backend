package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.CategorieDepense;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepenseDiverseRequest(

        @NotNull(message = "Le chauffeur est obligatoire")
        Long chauffeurId,

        Long vehiculeId,

        @NotNull(message = "La date est obligatoire")
        LocalDateTime dateDepense,

        @NotNull(message = "La catégorie est obligatoire")
        CategorieDepense categorie,

        @Size(max = 255)
        String description,

        @NotNull(message = "Le montant TTC est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être > 0")
        BigDecimal amountTTC,

        @Size(max = 100)
        String receiptNumber,

        @Size(max = 1000)
        String notes
) {}
