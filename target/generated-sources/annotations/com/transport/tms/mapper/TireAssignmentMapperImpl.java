package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Tire;
import com.transport.tms.domain.entity.TireAssignment;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.TirePosition;
import com.transport.tms.dto.request.TireAssignmentRequest;
import com.transport.tms.dto.response.TireAssignmentResponse;
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
public class TireAssignmentMapperImpl implements TireAssignmentMapper {

    @Override
    public TireAssignmentResponse toResponse(TireAssignment entity) {
        if ( entity == null ) {
            return null;
        }

        Long tireId = null;
        String tireSerialNumber = null;
        Long vehicleId = null;
        String vehicleRegistration = null;
        Long id = null;
        TirePosition position = null;
        LocalDate mountDate = null;
        BigDecimal mountMileage = null;
        LocalDate unmountDate = null;
        BigDecimal unmountMileage = null;
        String reasonUnmount = null;
        String notes = null;
        Instant createdAt = null;

        tireId = entityTireId( entity );
        tireSerialNumber = entityTireSerialNumber( entity );
        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        id = entity.getId();
        position = entity.getPosition();
        mountDate = entity.getMountDate();
        mountMileage = entity.getMountMileage();
        unmountDate = entity.getUnmountDate();
        unmountMileage = entity.getUnmountMileage();
        reasonUnmount = entity.getReasonUnmount();
        notes = entity.getNotes();
        createdAt = entity.getCreatedAt();

        TireAssignmentResponse tireAssignmentResponse = new TireAssignmentResponse( id, tireId, tireSerialNumber, vehicleId, vehicleRegistration, position, mountDate, mountMileage, unmountDate, unmountMileage, reasonUnmount, notes, createdAt );

        return tireAssignmentResponse;
    }

    @Override
    public TireAssignment toEntity(TireAssignmentRequest request) {
        if ( request == null ) {
            return null;
        }

        TireAssignment.TireAssignmentBuilder tireAssignment = TireAssignment.builder();

        tireAssignment.position( request.position() );
        tireAssignment.mountDate( request.mountDate() );
        tireAssignment.mountMileage( request.mountMileage() );
        tireAssignment.unmountDate( request.unmountDate() );
        tireAssignment.unmountMileage( request.unmountMileage() );
        tireAssignment.reasonUnmount( request.reasonUnmount() );
        tireAssignment.notes( request.notes() );

        return tireAssignment.build();
    }

    @Override
    public void updateEntity(TireAssignmentRequest request, TireAssignment entity) {
        if ( request == null ) {
            return;
        }

        entity.setPosition( request.position() );
        entity.setMountDate( request.mountDate() );
        entity.setMountMileage( request.mountMileage() );
        entity.setUnmountDate( request.unmountDate() );
        entity.setUnmountMileage( request.unmountMileage() );
        entity.setReasonUnmount( request.reasonUnmount() );
        entity.setNotes( request.notes() );
    }

    private Long entityTireId(TireAssignment tireAssignment) {
        Tire tire = tireAssignment.getTire();
        if ( tire == null ) {
            return null;
        }
        return tire.getId();
    }

    private String entityTireSerialNumber(TireAssignment tireAssignment) {
        Tire tire = tireAssignment.getTire();
        if ( tire == null ) {
            return null;
        }
        return tire.getSerialNumber();
    }

    private Long entityVehicleId(TireAssignment tireAssignment) {
        Vehicle vehicle = tireAssignment.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(TireAssignment tireAssignment) {
        Vehicle vehicle = tireAssignment.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }
}
