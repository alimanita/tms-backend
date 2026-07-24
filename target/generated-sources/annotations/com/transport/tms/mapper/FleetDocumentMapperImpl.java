package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.domain.entity.FleetDocument;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.DocumentType;
import com.transport.tms.dto.request.FleetDocumentRequest;
import com.transport.tms.dto.response.FleetDocumentResponse;
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
public class FleetDocumentMapperImpl implements FleetDocumentMapper {

    @Override
    public FleetDocumentResponse toResponse(FleetDocument entity) {
        if ( entity == null ) {
            return null;
        }

        Long vehicleId = null;
        String vehicleRegistration = null;
        Long driverId = null;
        String driverName = null;
        Long id = null;
        DocumentType documentType = null;
        String referenceNumber = null;
        String issuer = null;
        LocalDate issueDate = null;
        LocalDate expiryDate = null;
        BigDecimal amount = null;
        String filePath = null;
        String fileName = null;
        String status = null;
        String notes = null;
        Instant createdAt = null;

        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        driverId = entityDriverId( entity );
        driverName = entityDriverFullName( entity );
        id = entity.getId();
        documentType = entity.getDocumentType();
        referenceNumber = entity.getReferenceNumber();
        issuer = entity.getIssuer();
        issueDate = entity.getIssueDate();
        expiryDate = entity.getExpiryDate();
        amount = entity.getAmount();
        filePath = entity.getFilePath();
        fileName = entity.getFileName();
        status = entity.getStatus();
        notes = entity.getNotes();
        createdAt = entity.getCreatedAt();

        FleetDocumentResponse fleetDocumentResponse = new FleetDocumentResponse( id, vehicleId, vehicleRegistration, driverId, driverName, documentType, referenceNumber, issuer, issueDate, expiryDate, amount, filePath, fileName, status, notes, createdAt );

        return fleetDocumentResponse;
    }

    @Override
    public FleetDocument toEntity(FleetDocumentRequest request) {
        if ( request == null ) {
            return null;
        }

        FleetDocument.FleetDocumentBuilder fleetDocument = FleetDocument.builder();

        fleetDocument.documentType( request.documentType() );
        fleetDocument.referenceNumber( request.referenceNumber() );
        fleetDocument.issuer( request.issuer() );
        fleetDocument.issueDate( request.issueDate() );
        fleetDocument.expiryDate( request.expiryDate() );
        fleetDocument.amount( request.amount() );
        fleetDocument.status( request.status() );
        fleetDocument.notes( request.notes() );

        return fleetDocument.build();
    }

    @Override
    public void updateEntity(FleetDocumentRequest request, FleetDocument entity) {
        if ( request == null ) {
            return;
        }

        entity.setDocumentType( request.documentType() );
        entity.setReferenceNumber( request.referenceNumber() );
        entity.setIssuer( request.issuer() );
        entity.setIssueDate( request.issueDate() );
        entity.setExpiryDate( request.expiryDate() );
        entity.setAmount( request.amount() );
        entity.setStatus( request.status() );
        entity.setNotes( request.notes() );
    }

    private Long entityVehicleId(FleetDocument fleetDocument) {
        Vehicle vehicle = fleetDocument.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(FleetDocument fleetDocument) {
        Vehicle vehicle = fleetDocument.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }

    private Long entityDriverId(FleetDocument fleetDocument) {
        Driver driver = fleetDocument.getDriver();
        if ( driver == null ) {
            return null;
        }
        return driver.getId();
    }

    private String entityDriverFullName(FleetDocument fleetDocument) {
        Driver driver = fleetDocument.getDriver();
        if ( driver == null ) {
            return null;
        }
        return driver.getFullName();
    }
}
