package com.transport.tms.dto.fleet.request;

public record ChauffeurSlotRequest(
        Long chauffeurId,
        String heureDebut,
        String heureFin
) {
}
