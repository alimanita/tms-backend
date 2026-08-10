package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.PleinCarburant;
import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import org.springframework.stereotype.Component;

@Component
public class PleinCarburantMapper {

    public PleinCarburant toEntity(PleinCarburantRequest request) {
        PleinCarburant plein = new PleinCarburant();
        plein.setFillingDate(request.fillingDate());
        plein.setFuelType(request.fuelType());
        plein.setQuantityLiters(request.quantityLiters());
        plein.setPricePerLiter(request.pricePerLiter());
        plein.setMileageBefore(request.mileageBefore());
        plein.setMileageAfter(request.mileageAfter());
        plein.setIsFullTank(request.isFullTank() != null ? request.isFullTank() : true);
        plein.setReceiptNumber(request.receiptNumber());
        plein.setNotes(request.notes());
        plein.setAmountHT(request.amountHT());
        plein.setAmountTTC(request.amountTTC());
        plein.setTvaRate(request.tvaRate());
        plein.setTvaAmount(request.tvaAmount());
        plein.setIsTvaRecoverable(request.isTvaRecoverable() != null ? request.isTvaRecoverable() : false);
        plein.setRecoverableTvaAmount(request.recoverableTvaAmount());
        plein.setAcciseAmount(request.acciseAmount());
        plein.calculerConsommation();
        return plein;
    }

    public PleinCarburantResponse toResponse(PleinCarburant plein) {
        String chauffeurNom = null;
        if (plein.getChauffeur() != null) {
            chauffeurNom = plein.getChauffeur().getNom() + " " + plein.getChauffeur().getPrenom();
        }

        String proofUrl = plein.getProofFilePath() != null
                ? "/gestiondestock/v1/fleet/pleins-carburant/" + plein.getId() + "/proof"
                : null;

        return new PleinCarburantResponse(
                plein.getId(),
                plein.getReference(),
                plein.getVehicule().getId(),
                plein.getVehicule().getReference(),
                plein.getVehicule().getImmatriculation(),
                plein.getChauffeur() != null ? plein.getChauffeur().getId() : null,
                chauffeurNom,
                plein.getFillingDate(),
                plein.getFuelType(),
                plein.getQuantityLiters(),
                plein.getPricePerLiter(),
                plein.getTotalAmount(),
                plein.getMileageBefore(),
                plein.getMileageAfter(),
                plein.getDistanceSinceLast(),
                plein.getConsumptionRate(),
                plein.getIsFullTank(),
                plein.getReceiptNumber(),
                plein.getNotes(),
                proofUrl,
                plein.getCreatedAt(),
                plein.getAmountHT(),
                plein.getAmountTTC(),
                plein.getTvaRate(),
                plein.getTvaAmount(),
                plein.getIsTvaRecoverable(),
                plein.getRecoverableTvaAmount(),
                plein.getAcciseAmount()
        );
    }
}