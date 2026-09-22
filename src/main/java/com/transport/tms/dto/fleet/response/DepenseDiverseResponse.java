package com.transport.tms.dto.fleet.response;

import com.transport.tms.domain.enums.CategorieDepense;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepenseDiverseResponse(
        Long id,
        String reference,
        Long chauffeurId,
        String chauffeurNom,
        Long vehiculeId,
        String vehiculeImmatriculation,
        LocalDateTime dateDepense,
        CategorieDepense categorie,
        String description,
        BigDecimal amountTTC,
        String receiptNumber,
        String notes,
        String proofUrl,
        LocalDateTime createdAt
) {}
