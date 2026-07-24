package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.MissionExpenseType;

import java.math.BigDecimal;
import java.time.Instant;

public record MissionExpenseResponse(
        Long id,
        Long missionId,
        MissionExpenseType expenseType,
        BigDecimal amount,
        String currency,
        Instant expenseDate,
        String description,
        boolean reimbursable
) {}
