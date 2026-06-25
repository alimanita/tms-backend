package com.transport.tms.mapper;

import com.transport.tms.domain.entity.AmazonPurchase;
import com.transport.tms.domain.entity.AmazonPurchaseItem;
import com.transport.tms.dto.request.AmazonPurchaseRequest;
import com.transport.tms.dto.response.AmazonPurchaseResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class AmazonPurchaseMapperImpl implements AmazonPurchaseMapper {

    @Override
    public AmazonPurchaseResponse toResponse(AmazonPurchase entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String amazonOrderNumber = null;
        LocalDate purchaseDate = null;
        String supplier = null;
        BigDecimal amountHt = null;
        BigDecimal vatAmount = null;
        BigDecimal amountTtc = null;
        BigDecimal shippingCost = null;
        String currency = null;
        String status = null;
        String notes = null;
        List<AmazonPurchaseResponse.ItemResponse> items = null;

        id = entity.getId();
        amazonOrderNumber = entity.getAmazonOrderNumber();
        purchaseDate = entity.getPurchaseDate();
        supplier = entity.getSupplier();
        amountHt = entity.getAmountHt();
        vatAmount = entity.getVatAmount();
        amountTtc = entity.getAmountTtc();
        shippingCost = entity.getShippingCost();
        currency = entity.getCurrency();
        status = entity.getStatus();
        notes = entity.getNotes();
        items = amazonPurchaseItemListToItemResponseList( entity.getItems() );

        BigDecimal totalPurchaseCost = calculateTotalCost(entity);
        BigDecimal averageItemCost = calculateAverageCost(entity);

        AmazonPurchaseResponse amazonPurchaseResponse = new AmazonPurchaseResponse( id, amazonOrderNumber, purchaseDate, supplier, amountHt, vatAmount, amountTtc, shippingCost, currency, status, notes, totalPurchaseCost, averageItemCost, items );

        return amazonPurchaseResponse;
    }

    @Override
    public AmazonPurchaseResponse.ItemResponse toItemResponse(AmazonPurchaseItem item) {
        if ( item == null ) {
            return null;
        }

        Long id = null;
        String reference = null;
        String designation = null;
        BigDecimal quantity = null;
        BigDecimal unitPrice = null;
        BigDecimal totalPrice = null;
        BigDecimal weightKg = null;
        BigDecimal volumeM3 = null;

        id = item.getId();
        reference = item.getReference();
        designation = item.getDesignation();
        quantity = item.getQuantity();
        unitPrice = item.getUnitPrice();
        totalPrice = item.getTotalPrice();
        weightKg = item.getWeightKg();
        volumeM3 = item.getVolumeM3();

        AmazonPurchaseResponse.ItemResponse itemResponse = new AmazonPurchaseResponse.ItemResponse( id, reference, designation, quantity, unitPrice, totalPrice, weightKg, volumeM3 );

        return itemResponse;
    }

    @Override
    public AmazonPurchase toEntity(AmazonPurchaseRequest request) {
        if ( request == null ) {
            return null;
        }

        AmazonPurchase.AmazonPurchaseBuilder amazonPurchase = AmazonPurchase.builder();

        amazonPurchase.amazonOrderNumber( request.amazonOrderNumber() );
        amazonPurchase.purchaseDate( request.purchaseDate() );
        amazonPurchase.supplier( request.supplier() );
        amazonPurchase.amountHt( request.amountHt() );
        amazonPurchase.vatAmount( request.vatAmount() );
        amazonPurchase.amountTtc( request.amountTtc() );
        amazonPurchase.shippingCost( request.shippingCost() );
        amazonPurchase.currency( request.currency() );
        amazonPurchase.status( request.status() );
        amazonPurchase.notes( request.notes() );

        return amazonPurchase.build();
    }

    protected List<AmazonPurchaseResponse.ItemResponse> amazonPurchaseItemListToItemResponseList(List<AmazonPurchaseItem> list) {
        if ( list == null ) {
            return null;
        }

        List<AmazonPurchaseResponse.ItemResponse> list1 = new ArrayList<AmazonPurchaseResponse.ItemResponse>( list.size() );
        for ( AmazonPurchaseItem amazonPurchaseItem : list ) {
            list1.add( toItemResponse( amazonPurchaseItem ) );
        }

        return list1;
    }
}
