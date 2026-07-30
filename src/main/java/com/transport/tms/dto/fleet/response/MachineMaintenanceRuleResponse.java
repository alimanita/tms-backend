package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.enums.TypeActionMaintenance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MachineMaintenanceRuleResponse(

        Long id,

        Long machineId,
        String machineReference,
        String machineNom,

        String code,
        String description,
        TypeActionMaintenance typeAction,

        Integer intervalleHeures,
        Integer intervalleJours,

        String consommable,
        BigDecimal quantite,
        String uniteQuantite,

        BigDecimal dernieresHeuresEffectuees,
        LocalDate derniereDateEffectuee,

        Boolean prochaineEcheanceProche,
        BigDecimal heuresRestantes,

        Boolean actif,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}