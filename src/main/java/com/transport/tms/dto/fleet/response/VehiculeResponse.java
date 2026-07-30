package com.transport.tms.dto.fleet.response;


import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.domain.enums.TypeCarburant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehiculeResponse(
        Long id,
        String reference,
        String immatriculation,         // ✅
        String marque,                  // ✅
        String modele,                  // ✅
        Integer annee,
        TypeCarburant typeCarburant,
        StatutVehicule statut,
        BigDecimal kilometrageActuel,   // ✅
        BigDecimal capaciteReservoir,
        Long chauffeurAffecteId,
        String chauffeurNom,
        Boolean actif,                  // ✅
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}