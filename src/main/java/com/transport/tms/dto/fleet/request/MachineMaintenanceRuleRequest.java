package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.TypeActionMaintenance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.time.LocalDate;

public record MachineMaintenanceRuleRequest(

        @NotNull(message = "L'identifiant de la machine est obligatoire")
        Long machineId,

        String code,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @NotNull(message = "Le type d'action est obligatoire")
        TypeActionMaintenance typeAction,

        Integer intervalleHeures,

        Integer intervalleJours,

        String consommable,

        BigDecimal quantite,

        String uniteQuantite,

        BigDecimal dernieresHeuresEffectuees,

        LocalDate derniereDateEffectuee,

        Boolean actif
) {
}