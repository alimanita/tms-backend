package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.domain.entity.FuelRecord;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.response.FuelRecordResponse;
import java.math.BigDecimal;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class FuelRecordMapperImpl implements FuelRecordMapper {

    @Override
    public FuelRecordResponse toResponse(FuelRecord entity) {
        if ( entity == null ) {
            return null;
        }

        Long vehicleId = null;
        String vehicleRegistration = null;
        Long driverId = null;
        Long id = null;
        Instant fillDate = null;
        BigDecimal mileage = null;
        String station = null;
        BigDecimal liters = null;
        BigDecimal pricePerLiter = null;
        BigDecimal totalAmount = null;

        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        driverId = entityDriverId( entity );
        id = entity.getId();
        fillDate = entity.getFillDate();
        mileage = entity.getMileage();
        station = entity.getStation();
        liters = entity.getLiters();
        pricePerLiter = entity.getPricePerLiter();
        totalAmount = entity.getTotalAmount();

        String driverName = entity.getDriver() != null ? entity.getDriver().getFullName() : null;

        FuelRecordResponse fuelRecordResponse = new FuelRecordResponse( id, vehicleId, vehicleRegistration, driverId, driverName, fillDate, mileage, station, liters, pricePerLiter, totalAmount );

        return fuelRecordResponse;
    }

    private Long entityVehicleId(FuelRecord fuelRecord) {
        Vehicle vehicle = fuelRecord.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(FuelRecord fuelRecord) {
        Vehicle vehicle = fuelRecord.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }

    private Long entityDriverId(FuelRecord fuelRecord) {
        Driver driver = fuelRecord.getDriver();
        if ( driver == null ) {
            return null;
        }
        return driver.getId();
    }
}
