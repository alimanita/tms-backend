package com.transport.tms.dto.fleet.request;

import java.util.List;

public record ChauffeurConfigRequest(
        Boolean isGlobal,           // true = tous les chauffeurs, false = liste spécifique
        List<Long> chauffeurIds,    // ignoré si isGlobal=true
        Boolean showTarif,
        Boolean showCout,
        Boolean showCarburant
) {}
