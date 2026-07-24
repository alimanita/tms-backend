package com.transport.tms.mapper;

import com.transport.tms.domain.entity.OilChange;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.OilChangeRequest;
import com.transport.tms.dto.response.OilChangeResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class OilChangeMapperImpl implements OilChangeMapper {

    @Override
    public OilChangeResponse toResponse(OilChange entity) {
        if ( entity == null ) {
            return null;
        }

        Long vehicleId = null;
        String vehicleRegistration = null;
        Long id = null;
        String oilType = null;
        LocalDate changeDate = null;
        BigDecimal mileageAtChange = null;
        BigDecimal quantityLiters = null;
        BigDecimal unitCost = null;
        BigDecimal totalCost = null;
        BigDecimal nextChangeKm = null;
        LocalDate nextChangeDate = null;
        String performedBy = null;
        String notes = null;
        Instant createdAt = null;

        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        id = entity.getId();
        oilType = entity.getOilType();
        changeDate = entity.getChangeDate();
        mileageAtChange = entity.getMileageAtChange();
        quantityLiters = entity.getQuantityLiters();
        unitCost = entity.getUnitCost();
        totalCost = entity.getTotalCost();
        nextChangeKm = entity.getNextChangeKm();
        nextChangeDate = entity.getNextChangeDate();
        performedBy = entity.getPerformedBy();
        notes = entity.getNotes();
        createdAt = entity.getCreatedAt();

        OilChangeResponse oilChangeResponse = new OilChangeResponse( id, vehicleId, vehicleRegistration, oilType, changeDate, mileageAtChange, quantityLiters, unitCost, totalCost, nextChangeKm, nextChangeDate, performedBy, notes, createdAt );

        return oilChangeResponse;
    }

    @Override
    public OilChange toEntity(OilChangeRequest request) {
        if ( request == null ) {
            return null;
        }

        OilChange.OilChangeBuilder oilChange = OilChange.builder();

        oilChange.oilType( request.oilType() );
        oilChange.changeDate( request.changeDate() );
        oilChange.mileageAtChange( request.mileageAtChange() );
        oilChange.quantityLiters( request.quantityLiters() );
        oilChange.unitCost( request.unitCost() );
        oilChange.totalCost( request.totalCost() );
        oilChange.nextChangeKm( request.nextChangeKm() );
        oilChange.nextChangeDate( request.nextChangeDate() );
        oilChange.performedBy( request.performedBy() );
        oilChange.notes( request.notes() );

        return oilChange.build();
    }

    @Override
    public void updateEntity(OilChangeRequest request, OilChange entity) {
        if ( request == null ) {
            return;
        }

        entity.setOilType( request.oilType() );
        entity.setChangeDate( request.changeDate() );
        entity.setMileageAtChange( request.mileageAtChange() );
        entity.setQuantityLiters( request.quantityLiters() );
        entity.setUnitCost( request.unitCost() );
        entity.setTotalCost( request.totalCost() );
        entity.setNextChangeKm( request.nextChangeKm() );
        entity.setNextChangeDate( request.nextChangeDate() );
        entity.setPerformedBy( request.performedBy() );
        entity.setNotes( request.notes() );
    }

    private Long entityVehicleId(OilChange oilChange) {
        Vehicle vehicle = oilChange.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(OilChange oilChange) {
        Vehicle vehicle = oilChange.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }
}
