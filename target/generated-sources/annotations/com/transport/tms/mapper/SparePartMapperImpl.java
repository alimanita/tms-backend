package com.transport.tms.mapper;

import com.transport.tms.domain.entity.SparePart;
import com.transport.tms.dto.request.SparePartRequest;
import com.transport.tms.dto.response.SparePartResponse;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class SparePartMapperImpl implements SparePartMapper {

    @Override
    public SparePartResponse toResponse(SparePart entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String reference = null;
        String designation = null;
        String category = null;
        String supplier = null;
        BigDecimal purchasePrice = null;
        BigDecimal stockQty = null;
        BigDecimal minThreshold = null;
        boolean active = false;

        id = entity.getId();
        reference = entity.getReference();
        designation = entity.getDesignation();
        category = entity.getCategory();
        supplier = entity.getSupplier();
        purchasePrice = entity.getPurchasePrice();
        stockQty = entity.getStockQty();
        minThreshold = entity.getMinThreshold();
        active = entity.isActive();

        SparePartResponse sparePartResponse = new SparePartResponse( id, reference, designation, category, supplier, purchasePrice, stockQty, minThreshold, active );

        return sparePartResponse;
    }

    @Override
    public SparePart toEntity(SparePartRequest request) {
        if ( request == null ) {
            return null;
        }

        SparePart.SparePartBuilder sparePart = SparePart.builder();

        sparePart.reference( request.reference() );
        sparePart.designation( request.designation() );
        sparePart.category( request.category() );
        sparePart.supplier( request.supplier() );
        sparePart.purchasePrice( request.purchasePrice() );
        sparePart.stockQty( request.stockQty() );
        sparePart.minThreshold( request.minThreshold() );

        sparePart.active( true );

        return sparePart.build();
    }

    @Override
    public void updateEntity(SparePartRequest request, SparePart entity) {
        if ( request == null ) {
            return;
        }

        entity.setReference( request.reference() );
        entity.setDesignation( request.designation() );
        entity.setCategory( request.category() );
        entity.setSupplier( request.supplier() );
        entity.setPurchasePrice( request.purchasePrice() );
        entity.setStockQty( request.stockQty() );
        entity.setMinThreshold( request.minThreshold() );
    }
}
