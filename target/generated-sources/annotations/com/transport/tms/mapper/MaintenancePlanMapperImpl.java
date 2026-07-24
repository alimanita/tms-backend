package com.transport.tms.mapper;

import com.transport.tms.domain.entity.MaintenancePlan;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.MaintenanceTriggerType;
import com.transport.tms.domain.enums.MaintenanceType;
import com.transport.tms.dto.request.MaintenancePlanRequest;
import com.transport.tms.dto.response.MaintenancePlanResponse;
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
public class MaintenancePlanMapperImpl implements MaintenancePlanMapper {

    @Override
    public MaintenancePlanResponse toResponse(MaintenancePlan entity) {
        if ( entity == null ) {
            return null;
        }

        Long vehicleId = null;
        String vehicleRegistration = null;
        Long id = null;
        MaintenanceType maintenanceType = null;
        MaintenanceTriggerType triggerType = null;
        BigDecimal triggerValue = null;
        LocalDate lastPerformedDate = null;
        BigDecimal lastPerformedKm = null;
        LocalDate nextDueDate = null;
        BigDecimal nextDueKm = null;
        BigDecimal alertThreshold = null;
        boolean active = false;
        Instant createdAt = null;

        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        id = entity.getId();
        maintenanceType = entity.getMaintenanceType();
        triggerType = entity.getTriggerType();
        triggerValue = entity.getTriggerValue();
        lastPerformedDate = entity.getLastPerformedDate();
        lastPerformedKm = entity.getLastPerformedKm();
        nextDueDate = entity.getNextDueDate();
        nextDueKm = entity.getNextDueKm();
        alertThreshold = entity.getAlertThreshold();
        active = entity.isActive();
        createdAt = entity.getCreatedAt();

        MaintenancePlanResponse maintenancePlanResponse = new MaintenancePlanResponse( id, vehicleId, vehicleRegistration, maintenanceType, triggerType, triggerValue, lastPerformedDate, lastPerformedKm, nextDueDate, nextDueKm, alertThreshold, active, createdAt );

        return maintenancePlanResponse;
    }

    @Override
    public MaintenancePlan toEntity(MaintenancePlanRequest request) {
        if ( request == null ) {
            return null;
        }

        MaintenancePlan.MaintenancePlanBuilder maintenancePlan = MaintenancePlan.builder();

        maintenancePlan.maintenanceType( request.maintenanceType() );
        maintenancePlan.triggerType( request.triggerType() );
        maintenancePlan.triggerValue( request.triggerValue() );
        maintenancePlan.lastPerformedDate( request.lastPerformedDate() );
        maintenancePlan.lastPerformedKm( request.lastPerformedKm() );
        maintenancePlan.nextDueDate( request.nextDueDate() );
        maintenancePlan.nextDueKm( request.nextDueKm() );
        maintenancePlan.alertThreshold( request.alertThreshold() );
        maintenancePlan.active( request.active() );

        return maintenancePlan.build();
    }

    @Override
    public void updateEntity(MaintenancePlanRequest request, MaintenancePlan entity) {
        if ( request == null ) {
            return;
        }

        entity.setMaintenanceType( request.maintenanceType() );
        entity.setTriggerType( request.triggerType() );
        entity.setTriggerValue( request.triggerValue() );
        entity.setLastPerformedDate( request.lastPerformedDate() );
        entity.setLastPerformedKm( request.lastPerformedKm() );
        entity.setNextDueDate( request.nextDueDate() );
        entity.setNextDueKm( request.nextDueKm() );
        entity.setAlertThreshold( request.alertThreshold() );
        entity.setActive( request.active() );
    }

    private Long entityVehicleId(MaintenancePlan maintenancePlan) {
        Vehicle vehicle = maintenancePlan.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(MaintenancePlan maintenancePlan) {
        Vehicle vehicle = maintenancePlan.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }
}
