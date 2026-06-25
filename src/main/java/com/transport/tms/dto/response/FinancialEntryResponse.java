package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialEntryResponse(
        Long id,
        LocalDate entryDate,
        String entryType,
        String category,
        BigDecimal amount,
        String description
) {}
