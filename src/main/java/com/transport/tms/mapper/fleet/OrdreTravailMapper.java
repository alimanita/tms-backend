package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.fleet.OTMainOeuvre;
import com.transport.tms.domain.entity.fleet.OTPieceRechange;
import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.request.OrdreTravailRequest;
import com.transport.tms.dto.fleet.response.OTMainOeuvreResponse;
import com.transport.tms.dto.fleet.response.OTPieceRechangeResponse;
import com.transport.tms.dto.fleet.response.OrdreTravailResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdreTravailMapper {

    public OrdreTravail toEntity(OrdreTravailRequest request) {
        OrdreTravail ordre = new OrdreTravail();
        ordre.setEntityType(request.entityType());
        ordre.setEntityId(request.entityId());
        ordre.setTypeMaintenance(request.typeMaintenance());
        ordre.setTypeOrdre(request.typeOrdre());
        ordre.setPriorite(request.priorite() != null
                ? request.priorite()
                : OrdreTravail.PrioriteOT.NORMAL);
        ordre.setDescription(request.description());
        ordre.setReportedBy(request.reportedBy());
        ordre.setReportedDate(request.reportedDate());
        ordre.setScheduledDate(request.scheduledDate());
        ordre.setMileageAtOrder(request.mileageAtOrder());
        ordre.setHoursAtOrder(request.hoursAtOrder());
        ordre.setTechnicianId(request.technicianId());
        ordre.setWorkshop(request.workshop());
        ordre.setIsExternal(request.isExternal() != null ? request.isExternal() : false);
        ordre.setExternalProvider(request.externalProvider());
        ordre.setEstimatedCost(request.estimatedCost());
        ordre.setNotes(request.notes());
        return ordre;
    }

    public void updateEntity(OrdreTravail ordre, OrdreTravailRequest request) {
        ordre.setDescription(request.description());
        ordre.setScheduledDate(request.scheduledDate());
        ordre.setTechnicianId(request.technicianId());
        ordre.setWorkshop(request.workshop());
        ordre.setIsExternal(request.isExternal() != null ? request.isExternal() : false);
        ordre.setExternalProvider(request.externalProvider());
        ordre.setEstimatedCost(request.estimatedCost());
        ordre.setPriorite(request.priorite() != null
                ? request.priorite()
                : OrdreTravail.PrioriteOT.NORMAL);
        ordre.setNotes(request.notes());
    }

    public OrdreTravailResponse toResponse(OrdreTravail ordre, String entityRef) {
        return new OrdreTravailResponse(
                ordre.getId(),
                ordre.getReference(),
                ordre.getEntityType(),
                ordre.getEntityId(),
                entityRef,
                ordre.getTypeMaintenance(),
                ordre.getTypeMaintenance() != null
                        ? ordre.getTypeMaintenance().getLabel()
                        : null,
                ordre.getTypeOrdre(),
                ordre.getPriorite(),
                ordre.getStatut(),
                ordre.getDescription(),
                ordre.getReportedDate(),
                ordre.getScheduledDate(),
                ordre.getStartedAt(),
                ordre.getCompletedAt(),
                ordre.getMileageAtOrder(),
                ordre.getHoursAtOrder(),
                ordre.getWorkshop(),
                ordre.getIsExternal(),
                ordre.getExternalProvider(),
                ordre.getEstimatedCost(),
                ordre.getActualLaborCost(),
                ordre.getActualPartsCost(),
                ordre.getActualTotalCost(),
                ordre.getDowntimeHours(),
                toPieceResponseList(ordre.getPieces()),
                toMainOeuvreResponseList(ordre.getMainOeuvres()),
                ordre.getNotes(),
                ordre.getCreatedAt(),
                ordre.getUpdatedAt()
        );
    }

    // ── Pièces ────────────────────────────────────────────────

    public OTPieceRechangeResponse toPieceResponse(OTPieceRechange piece) {
        return new OTPieceRechangeResponse(
                piece.getId(),
                piece.getPieceRechange().getId(),
                piece.getPieceRechange().getReference(),
                piece.getPieceRechange().getName(),
                piece.getQuantityPlanned(),
                piece.getQuantityUsed(),
                piece.getUnitCost(),
                piece.getTotalCost()
        );
    }

    public List<OTPieceRechangeResponse> toPieceResponseList(List<OTPieceRechange> pieces) {
        if (pieces == null) return List.of();
        return pieces.stream().map(this::toPieceResponse).toList();
    }

    // ── Main d'œuvre ──────────────────────────────────────────

    public OTMainOeuvreResponse toMainOeuvreResponse(OTMainOeuvre mo) {
        return new OTMainOeuvreResponse(
                mo.getId(),
                mo.getTechnicianName(),
                mo.getIsExternal(),
                mo.getHoursPlanned(),
                mo.getHoursActual(),
                mo.getHourlyRate(),
                mo.getTotalCost()
        );
    }

    public List<OTMainOeuvreResponse> toMainOeuvreResponseList(List<OTMainOeuvre> list) {
        if (list == null) return List.of();
        return list.stream().map(this::toMainOeuvreResponse).toList();
    }
}