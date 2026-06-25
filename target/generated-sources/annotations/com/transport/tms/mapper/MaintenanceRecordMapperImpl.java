package com.transport.tms.mapper;

import com.transport.tms.domain.entity.MaintenanceRecord;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.response.MaintenanceRecordResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class MaintenanceRecordMapperImpl implements MaintenanceRecordMapper {

    @Override
    public MaintenanceRecordResponse toResponse(MaintenanceRecord entity) {
        if ( entity == null ) {
            return null;
        }

        Long vehicleId = null;
        String vehicleRegistration = null;
        Long id = null;
        String maintenanceType = null;
        LocalDate maintenanceDate = null;
        BigDecimal mileage = null;
        BigDecimal cost = null;
        String supplier = null;
        LocalDate nextDueDate = null;
        BigDecimal nextDueMileage = null;

        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        id = entity.getId();
        maintenanceType = entity.getMaintenanceType();
        maintenanceDate = entity.getMaintenanceDate();
        mileage = entity.getMileage();
        cost = entity.getCost();
        supplier = entity.getSupplier();
        nextDueDate = entity.getNextDueDate();
        nextDueMileage = entity.getNextDueMileage();

        MaintenanceRecordResponse maintenanceRecordResponse = new MaintenanceRecordResponse( id, vehicleId, vehicleRegistration, maintenanceType, maintenanceDate, mileage, cost, supplier, nextDueDate, nextDueMileage );

        return maintenanceRecordResponse;
    }

    private Long entityVehicleId(MaintenanceRecord maintenanceRecord) {
        Vehicle vehicle = maintenanceRecord.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(MaintenanceRecord maintenanceRecord) {
        Vehicle vehicle = maintenanceRecord.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }
}
