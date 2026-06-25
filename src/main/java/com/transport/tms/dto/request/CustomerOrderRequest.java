package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.CustomerOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerOrderRequest(
        @NotBlank String reference,
        @NotNull LocalDate orderDate,
        @NotNull Long customerId,
        @NotNull CustomerOrderStatus status,
        String notes,
        @NotEmpty @Valid List<LineRequest> lines
) {
    public record LineRequest(
            String productRef,
            @NotBlank String designation,
            @NotNull BigDecimal quantity,
            @NotNull BigDecimal salePrice
    ) {}
}
