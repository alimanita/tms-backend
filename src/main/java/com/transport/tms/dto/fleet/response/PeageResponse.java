package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PeageResponse(
        Long id,
        String reference,
        Long vehiculeId,
        String vehiculeImmatriculation,
        Long chauffeurId,
        String chauffeurNom,
        Long missionId,
        String missionReference,
        LocalDateTime datePassage,
        BigDecimal amountHT,
        BigDecimal tvaRate,
        BigDecimal tvaAmount,
        BigDecimal amountTTC,
        String gareEntree,
        String gareSortie,
        String receiptNumber,
        String societeAutoroute,
        String notes,
        String proofUrl,
        LocalDateTime createdAt
) {}
