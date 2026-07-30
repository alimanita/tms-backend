package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.TypeCarburant;
import jakarta.validation.constraints.NotBlank;


import java.math.BigDecimal;

public record VehiculeRequest(


        String reference,

        @NotBlank(message = "L'immatriculation est obligatoire")
        String immatriculation,         // ✅

        String marque,                  // ✅
        String modele,                  // ✅
        Integer annee,

        TypeCarburant typeCarburant,

        BigDecimal kilometrageActuel,   // ✅
        BigDecimal capaciteReservoir,
Integer idEntreprise,
        Long chauffeurAffecteId
) {}