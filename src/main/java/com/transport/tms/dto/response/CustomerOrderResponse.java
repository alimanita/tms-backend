package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.CustomerOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerOrderResponse(
        Long id,
        String reference,
        LocalDate orderDate,
        Long customerId,
        String customerName,
        CustomerOrderStatus status,
        BigDecimal totalAmount,
        String notes,
        List<LineResponse> lines
) {
    public record LineResponse(
            Long id,
            String productRef,
            String designation,
            BigDecimal quantity,
            BigDecimal salePrice,
            BigDecimal totalPrice
    ) {}
}
