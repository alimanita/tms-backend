package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Tire;
import com.transport.tms.domain.enums.TireStatus;
import com.transport.tms.dto.request.TireRequest;
import com.transport.tms.dto.response.TireResponse;
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
public class TireMapperImpl implements TireMapper {

    @Override
    public TireResponse toResponse(Tire entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String serialNumber = null;
        String brand = null;
        String model = null;
        String size = null;
        String type = null;
        LocalDate purchaseDate = null;
        BigDecimal purchaseCost = null;
        BigDecimal maxKm = null;
        TireStatus status = null;
        boolean active = false;
        Instant createdAt = null;

        id = entity.getId();
        serialNumber = entity.getSerialNumber();
        brand = entity.getBrand();
        model = entity.getModel();
        size = entity.getSize();
        type = entity.getType();
        purchaseDate = entity.getPurchaseDate();
        purchaseCost = entity.getPurchaseCost();
        maxKm = entity.getMaxKm();
        status = entity.getStatus();
        active = entity.isActive();
        createdAt = entity.getCreatedAt();

        TireResponse tireResponse = new TireResponse( id, serialNumber, brand, model, size, type, purchaseDate, purchaseCost, maxKm, status, active, createdAt );

        return tireResponse;
    }

    @Override
    public Tire toEntity(TireRequest request) {
        if ( request == null ) {
            return null;
        }

        Tire.TireBuilder tire = Tire.builder();

        tire.serialNumber( request.serialNumber() );
        tire.brand( request.brand() );
        tire.model( request.model() );
        tire.size( request.size() );
        tire.type( request.type() );
        tire.purchaseDate( request.purchaseDate() );
        tire.purchaseCost( request.purchaseCost() );
        tire.maxKm( request.maxKm() );
        tire.status( request.status() );

        tire.active( true );

        return tire.build();
    }

    @Override
    public void updateEntity(TireRequest request, Tire entity) {
        if ( request == null ) {
            return;
        }

        entity.setSerialNumber( request.serialNumber() );
        entity.setBrand( request.brand() );
        entity.setModel( request.model() );
        entity.setSize( request.size() );
        entity.setType( request.type() );
        entity.setPurchaseDate( request.purchaseDate() );
        entity.setPurchaseCost( request.purchaseCost() );
        entity.setMaxKm( request.maxKm() );
        entity.setStatus( request.status() );
    }
}
