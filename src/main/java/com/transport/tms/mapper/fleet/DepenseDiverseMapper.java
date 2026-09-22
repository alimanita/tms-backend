package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.fleet.DepenseDiverse;
import com.transport.tms.dto.fleet.request.DepenseDiverseRequest;
import com.transport.tms.dto.fleet.response.DepenseDiverseResponse;
import org.springframework.stereotype.Component;

@Component
public class DepenseDiverseMapper {

    public DepenseDiverse toEntity(DepenseDiverseRequest request) {
        DepenseDiverse d = new DepenseDiverse();
        d.setDateDepense(request.dateDepense());
        d.setCategorie(request.categorie());
        d.setDescription(request.description());
        d.setAmountTTC(request.amountTTC());
        d.setReceiptNumber(request.receiptNumber());
        d.setNotes(request.notes());
        return d;
    }

    public DepenseDiverseResponse toResponse(DepenseDiverse d) {
        String chauffeurNom = null;
        if (d.getChauffeur() != null) {
            chauffeurNom = d.getChauffeur().getNom() + " " + d.getChauffeur().getPrenom();
        }

        String vehiculeImmat = null;
        if (d.getVehicule() != null) {
            vehiculeImmat = d.getVehicule().getImmatriculation();
        }

        String proofUrl = d.getProofFilePath() != null
                ? "/gestiondestock/v1/fleet/depenses-diverses/" + d.getId() + "/proof"
                : null;

        return new DepenseDiverseResponse(
                d.getId(),
                d.getReference(),
                d.getChauffeur() != null ? d.getChauffeur().getId() : null,
                chauffeurNom,
                d.getVehicule() != null ? d.getVehicule().getId() : null,
                vehiculeImmat,
                d.getDateDepense(),
                d.getCategorie(),
                d.getDescription(),
                d.getAmountTTC(),
                d.getReceiptNumber(),
                d.getNotes(),
                proofUrl,
                d.getCreatedAt()
        );
    }
}
