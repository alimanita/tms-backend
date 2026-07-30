package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.StatutMachine;
import jakarta.validation.constraints.NotBlank;


import java.math.BigDecimal;
import java.time.LocalDate;

public record MachineRequest(
        String reference,

        @NotBlank(message = "Le numéro de série est obligatoire")
        String numeroSerie,

        @NotBlank(message = "Le nom de la machine est obligatoire")
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
        Boolean actif
) {}
