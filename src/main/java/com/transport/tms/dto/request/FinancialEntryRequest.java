package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialEntryRequest(
        @NotNull LocalDate entryDate,
        @NotBlank String entryType,
        @NotBlank String category,
        @NotNull BigDecimal amount,
        String description
) {}
