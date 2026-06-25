package com.transport.tms.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AmazonPurchaseRequest(
        @NotBlank String amazonOrderNumber,
        @NotNull LocalDate purchaseDate,
        String supplier,
        BigDecimal amountHt,
        BigDecimal vatAmount,
        BigDecimal amountTtc,
        BigDecimal shippingCost,
        String currency,
        String status,
        String notes,
        @NotEmpty @Valid List<ItemRequest> items
) {
    public record ItemRequest(
            String reference,
            @NotBlank String designation,
            @NotNull BigDecimal quantity,
            @NotNull BigDecimal unitPrice,
            BigDecimal weightKg,
            BigDecimal volumeM3
    ) {}
}
