package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Customer;
import com.transport.tms.domain.entity.CustomerOrder;
import com.transport.tms.domain.entity.Driver;
import com.transport.tms.domain.entity.TransportMission;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.MissionStatus;
import com.transport.tms.dto.response.TransportMissionResponse;
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
public class TransportMissionMapperImpl implements TransportMissionMapper {

    @Override
    public TransportMissionResponse toResponse(TransportMission entity) {
        if ( entity == null ) {
            return null;
        }

        Long customerOrderId = null;
        String customerOrderReference = null;
        Long customerId = null;
        String customerName = null;
        Long vehicleId = null;
        String vehicleRegistration = null;
        Long driverId = null;
        Long id = null;
        String reference = null;
        Instant departureDate = null;
        Instant expectedArrival = null;
        Instant actualArrival = null;
        String loadingAddress = null;
        String deliveryAddress = null;
        MissionStatus status = null;
        BigDecimal revenue = null;
        BigDecimal transportCost = null;
        String notes = null;

        customerOrderId = entityCustomerOrderId( entity );
        customerOrderReference = entityCustomerOrderReference( entity );
        customerId = entityCustomerId( entity );
        customerName = entityCustomerName( entity );
        vehicleId = entityVehicleId( entity );
        vehicleRegistration = entityVehicleRegistration( entity );
        driverId = entityDriverId( entity );
        id = entity.getId();
        reference = entity.getReference();
        departureDate = entity.getDepartureDate();
        expectedArrival = entity.getExpectedArrival();
        actualArrival = entity.getActualArrival();
        loadingAddress = entity.getLoadingAddress();
        deliveryAddress = entity.getDeliveryAddress();
        status = entity.getStatus();
        revenue = entity.getRevenue();
        transportCost = entity.getTransportCost();
        notes = entity.getNotes();

        String driverName = entity.getDriver() != null ? entity.getDriver().getFullName() : null;

        TransportMissionResponse transportMissionResponse = new TransportMissionResponse( id, reference, customerOrderId, customerOrderReference, customerId, customerName, vehicleId, vehicleRegistration, driverId, driverName, departureDate, expectedArrival, actualArrival, loadingAddress, deliveryAddress, status, revenue, transportCost, notes );

        return transportMissionResponse;
    }

    private Long entityCustomerOrderId(TransportMission transportMission) {
        CustomerOrder customerOrder = transportMission.getCustomerOrder();
        if ( customerOrder == null ) {
            return null;
        }
        return customerOrder.getId();
    }

    private String entityCustomerOrderReference(TransportMission transportMission) {
        CustomerOrder customerOrder = transportMission.getCustomerOrder();
        if ( customerOrder == null ) {
            return null;
        }
        return customerOrder.getReference();
    }

    private Long entityCustomerId(TransportMission transportMission) {
        Customer customer = transportMission.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getId();
    }

    private String entityCustomerName(TransportMission transportMission) {
        Customer customer = transportMission.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getName();
    }

    private Long entityVehicleId(TransportMission transportMission) {
        Vehicle vehicle = transportMission.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getId();
    }

    private String entityVehicleRegistration(TransportMission transportMission) {
        Vehicle vehicle = transportMission.getVehicle();
        if ( vehicle == null ) {
            return null;
        }
        return vehicle.getRegistration();
    }

    private Long entityDriverId(TransportMission transportMission) {
        Driver driver = transportMission.getDriver();
        if ( driver == null ) {
            return null;
        }
        return driver.getId();
    }
}
