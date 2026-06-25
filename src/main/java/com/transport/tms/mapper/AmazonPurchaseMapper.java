package com.transport.tms.mapper;

import com.transport.tms.domain.entity.AmazonPurchase;
import com.transport.tms.domain.entity.AmazonPurchaseItem;
import com.transport.tms.dto.request.AmazonPurchaseRequest;
import com.transport.tms.dto.response.AmazonPurchaseResponse;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AmazonPurchaseMapper {

    @Mapping(target = "totalPurchaseCost", expression = "java(calculateTotalCost(entity))")
    @Mapping(target = "averageItemCost", expression = "java(calculateAverageCost(entity))")
    AmazonPurchaseResponse toResponse(AmazonPurchase entity);

    AmazonPurchaseResponse.ItemResponse toItemResponse(AmazonPurchaseItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AmazonPurchase toEntity(AmazonPurchaseRequest request);

    default BigDecimal calculateTotalCost(AmazonPurchase entity) {
        BigDecimal itemsTotal = entity.getItems() == null ? BigDecimal.ZERO :
                entity.getItems().stream()
                        .map(AmazonPurchaseItem::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = entity.getShippingCost() == null ? BigDecimal.ZERO : entity.getShippingCost();
        return itemsTotal.add(shipping).setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal calculateAverageCost(AmazonPurchase entity) {
        if (entity.getItems() == null || entity.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = entity.getItems().stream()
                .map(AmazonPurchaseItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(entity.getItems().size()), 2, RoundingMode.HALF_UP);
    }
}
