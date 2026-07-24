package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.VehicleStatus;
import com.transport.tms.dto.request.VehicleRequest;
import com.transport.tms.dto.response.VehicleResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class VehicleMapperImpl implements VehicleMapper {

    @Override
    public VehicleResponse toResponse(Vehicle entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String registration = null;
        String vin = null;
        String brand = null;
        String model = null;
        Integer year = null;
        String vehicleType = null;
        BigDecimal payloadKg = null;
        BigDecimal currentMileage = null;
        LocalDate acquisitionDate = null;
        LocalDate insuranceExpiry = null;
        VehicleStatus status = null;
        boolean active = false;

        id = entity.getId();
        registration = entity.getRegistration();
        vin = entity.getVin();
        brand = entity.getBrand();
        model = entity.getModel();
        year = entity.getYear();
        vehicleType = entity.getVehicleType();
        payloadKg = entity.getPayloadKg();
        currentMileage = entity.getCurrentMileage();
        acquisitionDate = entity.getAcquisitionDate();
        insuranceExpiry = entity.getInsuranceExpiry();
        status = entity.getStatus();
        active = entity.isActive();

        VehicleResponse vehicleResponse = new VehicleResponse( id, registration, vin, brand, model, year, vehicleType, payloadKg, currentMileage, acquisitionDate, insuranceExpiry, status, active );

        return vehicleResponse;
    }

    @Override
    public Vehicle toEntity(VehicleRequest request) {
        if ( request == null ) {
            return null;
        }

        Vehicle.VehicleBuilder vehicle = Vehicle.builder();

        vehicle.registration( request.registration() );
        vehicle.vin( request.vin() );
        vehicle.brand( request.brand() );
        vehicle.model( request.model() );
        vehicle.year( request.year() );
        vehicle.vehicleType( request.vehicleType() );
        vehicle.payloadKg( request.payloadKg() );
        vehicle.currentMileage( request.currentMileage() );
        vehicle.acquisitionDate( request.acquisitionDate() );
        vehicle.insuranceExpiry( request.insuranceExpiry() );
        vehicle.status( request.status() );

        vehicle.active( true );

        return vehicle.build();
    }

    @Override
    public void updateEntity(VehicleRequest request, Vehicle entity) {
        if ( request == null ) {
            return;
        }

        entity.setRegistration( request.registration() );
        entity.setVin( request.vin() );
        entity.setBrand( request.brand() );
        entity.setModel( request.model() );
        entity.setYear( request.year() );
        entity.setVehicleType( request.vehicleType() );
        entity.setPayloadKg( request.payloadKg() );
        entity.setCurrentMileage( request.currentMileage() );
        entity.setAcquisitionDate( request.acquisitionDate() );
        entity.setInsuranceExpiry( request.insuranceExpiry() );
        entity.setStatus( request.status() );
    }
}
