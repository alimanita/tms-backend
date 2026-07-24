package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.MissionExpenseType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record MissionExpenseRequest(
        @NotNull MissionExpenseType expenseType,
        @NotNull @Positive BigDecimal amount,
        String currency,
        @NotNull Instant expenseDate,
        String description,
        Boolean reimbursable
) {}
