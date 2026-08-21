package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.DepenseMission;
import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.dto.fleet.request.DepenseMissionRequest;
import com.transport.tms.dto.fleet.request.MissionRequest;
import com.transport.tms.dto.fleet.response.DepenseMissionResponse;
import com.transport.tms.dto.fleet.response.MissionResponse;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper {

    public Mission toEntity(MissionRequest request) {
        Mission mission = new Mission();
        mission.setTitle(request.title());
        mission.setClientId(request.clientId());
        mission.setDepartureLocation(request.departureLocation());
        mission.setArrivalLocation(request.arrivalLocation());
        mission.setPlannedDeparture(request.plannedDeparture());
        mission.setPlannedReturn(request.plannedReturn());
        mission.setPurpose(request.purpose());
        mission.setCargoDescription(request.cargoDescription());
        mission.setCargoWeight(request.cargoWeight());
        mission.setNotes(request.notes());
        mission.setRevenue(request.revenue());
        return mission;
    }

    public void updateEntity(Mission mission, MissionRequest request) {
        mission.setTitle(request.title());
        mission.setClientId(request.clientId());
        mission.setDepartureLocation(request.departureLocation());
        mission.setArrivalLocation(request.arrivalLocation());
        mission.setPlannedDeparture(request.plannedDeparture());
        mission.setPlannedReturn(request.plannedReturn());
        mission.setPurpose(request.purpose());
        mission.setCargoDescription(request.cargoDescription());
        mission.setCargoWeight(request.cargoWeight());
        mission.setNotes(request.notes());
        mission.setRevenue(request.revenue());
    }

    public MissionResponse toResponse(Mission mission) {
        java.util.List<Long> chauffeurIds = new java.util.ArrayList<>();
        java.util.List<String> chauffeursNomsList = new java.util.ArrayList<>();
        if (mission.getChauffeurs() != null && !mission.getChauffeurs().isEmpty()) {
            for (com.transport.tms.domain.entity.fleet.Chauffeur c : mission.getChauffeurs()) {
                chauffeurIds.add(c.getId());
                chauffeursNomsList.add(c.getNom() + " " + c.getPrenom());
            }
        }
        String chauffeursNoms = String.join(", ", chauffeursNomsList);

        String letterUrl = null;
        if (mission.getLetterMissionPath() != null && !mission.getLetterMissionPath().isBlank()) {
            letterUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/fleet/missions/")
                    .path(mission.getId().toString())
                    .path("/letter")
                    .toUriString();
        }

        return new MissionResponse(
                mission.getId(),
                mission.getReference(),
                mission.getTitle(),
                mission.getClientId(),
                mission.getVehicule().getId(),
                mission.getVehicule().getReference(),
                mission.getVehicule().getImmatriculation(),
                chauffeurIds,
                chauffeursNoms,
                mission.getStatut(),
                mission.getDepartureLocation(),
                mission.getArrivalLocation(),
                mission.getPlannedDeparture(),
                mission.getPlannedReturn(),
                mission.getActualDeparture(),
                mission.getActualReturn(),
                mission.getPurpose(),
                mission.getCargoDescription(),
                mission.getCargoWeight(),
                mission.getMileageAtDeparture(),
                mission.getMileageAtReturn(),
                mission.getTotalKm(),
                mission.getFuelCost(),
                mission.getTollCost(),
                mission.getOtherExpenses(),
                mission.getTotalCost(),
                mission.getRevenue(),
                mission.getInvoiceId(),
                mission.getNotes(),
                mission.getApprovedBy(),
                mission.getApprovedAt(),
                mission.getCreatedAt(),
                mission.getUpdatedAt(),
                letterUrl
        );
    }

    // ── Dépenses ──────────────────────────────────────────────

    public DepenseMission toDepenseEntity(DepenseMissionRequest request) {
        DepenseMission depense = new DepenseMission();
        depense.setExpenseType(request.expenseType());
        depense.setMontant(request.montant());
        depense.setCurrency(request.currency() != null ? request.currency() : "TND");
        depense.setExpenseDate(request.expenseDate());
        depense.setDescription(request.description());
        depense.setReceiptPath(request.receiptPath());
        depense.setIsReimbursable(request.isReimbursable() != null
                ? request.isReimbursable()
                : true);
        
        // Champs TVA
        depense.setAmountHT(request.amountHT());
        depense.setTvaRate(request.tvaRate());
        depense.setTvaAmount(request.tvaAmount());
        depense.setIsTvaRecoverable(request.isTvaRecoverable() != null ? request.isTvaRecoverable() : false);
        
        return depense;
    }

    public DepenseMissionResponse toDepenseResponse(DepenseMission depense) {
        return new DepenseMissionResponse(
                depense.getId(),
                depense.getMission().getId(),
                depense.getMission().getReference(),
                depense.getExpenseType(),
                depense.getMontant(),
                depense.getCurrency(),
                depense.getExpenseDate(),
                depense.getDescription(),
                depense.getReceiptPath(),
                depense.getIsReimbursable(),
                depense.getAmountHT(),
                depense.getTvaRate(),
                depense.getTvaAmount(),
                depense.getIsTvaRecoverable(),
                depense.getCreatedAt()
        );
    }
}