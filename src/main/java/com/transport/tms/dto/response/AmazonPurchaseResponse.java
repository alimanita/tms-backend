package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AmazonPurchaseResponse(
        Long id,
        String amazonOrderNumber,
        LocalDate purchaseDate,
        String supplier,
        BigDecimal amountHt,
        BigDecimal vatAmount,
        BigDecimal amountTtc,
        BigDecimal shippingCost,
        String currency,
        String status,
        String notes,
        BigDecimal totalPurchaseCost,
        BigDecimal averageItemCost,
        List<ItemResponse> items
) {
    public record ItemResponse(
            Long id,
            String reference,
            String designation,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice,
            BigDecimal weightKg,
            BigDecimal volumeM3
    ) {}
}
