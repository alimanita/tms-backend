package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.fleet.Peage;
import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.response.PeageResponse;
import org.springframework.stereotype.Component;

@Component
public class PeageMapper {

    public Peage toEntity(PeageRequest request) {
        Peage peage = new Peage();
        peage.setDatePassage(request.datePassage());
        peage.setAmountHT(request.amountHT());
        peage.setTvaRate(request.tvaRate());
        peage.setTvaAmount(request.tvaAmount());
        peage.setAmountTTC(request.amountTTC());
        peage.setGareEntree(request.gareEntree());
        peage.setGareSortie(request.gareSortie());
        peage.setReceiptNumber(request.receiptNumber());
        peage.setSocieteAutoroute(request.societeAutoroute());
        peage.setNotes(request.notes());
        return peage;
    }

    public PeageResponse toResponse(Peage peage) {
        String chauffeurNom = null;
        if (peage.getChauffeur() != null) {
            chauffeurNom = peage.getChauffeur().getNom() + " " + peage.getChauffeur().getPrenom();
        }

        String proofUrl = peage.getProofFilePath() != null
                ? "/gestiondestock/v1/fleet/peages/" + peage.getId() + "/proof"
                : null;

        return new PeageResponse(
                peage.getId(),
                peage.getReference(),
                peage.getVehicule().getId(),
                peage.getVehicule().getImmatriculation(),
                peage.getChauffeur() != null ? peage.getChauffeur().getId() : null,
                chauffeurNom,
                peage.getMission() != null ? peage.getMission().getId() : null,
                peage.getMission() != null ? peage.getMission().getReference() : null,
                peage.getDatePassage(),
                peage.getAmountHT(),
                peage.getTvaRate(),
                peage.getTvaAmount(),
                peage.getAmountTTC(),
                peage.getGareEntree(),
                peage.getGareSortie(),
                peage.getReceiptNumber(),
                peage.getSocieteAutoroute(),
                peage.getNotes(),
                proofUrl,
                peage.getCreatedAt()
        );
    }
}
