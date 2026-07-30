package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.ChangementHuile;
import com.transport.tms.dto.fleet.request.ChangementHuileRequest;
import com.transport.tms.dto.fleet.response.ChangementHuileResponse;
import org.springframework.stereotype.Component;

@Component
public class ChangementHuileMapper {

    public ChangementHuile toEntity(ChangementHuileRequest request) {
        ChangementHuile ch = new ChangementHuile();
        ch.setEntityType(request.entityType());
        ch.setEntityId(request.entityId());
        ch.setTypeHuile(request.typeHuile());
        ch.setChangeDate(request.changeDate());
        ch.setMileageAtChange(request.mileageAtChange());
        ch.setHoursAtChange(request.hoursAtChange());
        ch.setQuantityLiters(request.quantityLiters());
        ch.setUnitCost(request.unitCost());
        ch.setTotalCost(request.totalCost());
        ch.setNextChangeKm(request.nextChangeKm());
        ch.setNextChangeHours(request.nextChangeHours());
        ch.setNextChangeDate(request.nextChangeDate());
        ch.setPerformedBy(request.performedBy());
        ch.setNotes(request.notes());
        return ch;
    }

    public void updateEntity(ChangementHuile ch, ChangementHuileRequest request) {
        ch.setTypeHuile(request.typeHuile());
        ch.setChangeDate(request.changeDate());
        ch.setMileageAtChange(request.mileageAtChange());
        ch.setHoursAtChange(request.hoursAtChange());
        ch.setQuantityLiters(request.quantityLiters());
        ch.setUnitCost(request.unitCost());
        ch.setTotalCost(request.totalCost());
        ch.setNextChangeKm(request.nextChangeKm());
        ch.setNextChangeHours(request.nextChangeHours());
        ch.setNextChangeDate(request.nextChangeDate());
        ch.setPerformedBy(request.performedBy());
        ch.setNotes(request.notes());
    }

    public ChangementHuileResponse toResponse(ChangementHuile ch, String entityRef) {
        return new ChangementHuileResponse(
                ch.getId(),
                ch.getReference(),
                ch.getEntityType(),
                ch.getEntityId(),
                entityRef,
                ch.getTypeHuile(),
                ch.getTypeHuile() != null ? ch.getTypeHuile().getLabel() : null,
                ch.getChangeDate(),
                ch.getMileageAtChange(),
                ch.getHoursAtChange(),
                ch.getQuantityLiters(),
                ch.getUnitCost(),
                ch.getTotalCost(),
                ch.getNextChangeKm(),
                ch.getNextChangeHours(),
                ch.getNextChangeDate(),
                ch.getPerformedBy(),
                ch.getNotes(),
                ch.getCreatedAt()
        );
    }
}