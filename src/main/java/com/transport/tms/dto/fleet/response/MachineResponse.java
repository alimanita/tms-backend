package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.enums.StatutMachine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MachineResponse(
        Long id,
        String reference,
        String numeroSerie,
        String nom,
        String marque,
        String modele,
        String categorie,
        
        LocalDate dateAchat,
        BigDecimal prixAchat,

        String unitesPuissance,
        BigDecimal valeurPuissance,
        BigDecimal heuresInitiales,
        BigDecimal heuresActuelles,
        String localisation,

        StatutMachine statut,
        BigDecimal tauxDisponibilite,

        String notes,
        Boolean actif,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
