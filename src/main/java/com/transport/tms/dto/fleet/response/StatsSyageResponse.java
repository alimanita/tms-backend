package com.transport.tms.dto.fleet.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Statistiques de syage depuis un changement de lames. */
public record StatsSyageResponse(
        Long otId,
        String referenceOT,
        Long machineId,
        String machineNom,
        LocalDateTime dateChangementLames,
        BigDecimal hauteurTotaleCm,
        BigDecimal hauteurTotaleMetres,
        Long nombreBlocsSyes,
        Long nombreOFsSyage
) {}
