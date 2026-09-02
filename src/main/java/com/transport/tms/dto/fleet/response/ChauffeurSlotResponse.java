package com.transport.tms.dto.fleet.response;

import java.time.LocalDateTime;

public record ChauffeurSlotResponse(
        Long chauffeurId,
        String nom,
        LocalDateTime heureDebut,
        LocalDateTime heureFin
) {
}
