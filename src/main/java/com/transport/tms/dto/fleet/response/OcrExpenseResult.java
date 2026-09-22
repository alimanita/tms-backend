package com.transport.tms.dto.fleet.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OcrExpenseResult(
        LocalDateTime dateDepense,
        BigDecimal amountTTC,
        String receiptNumber,
        String categorie,
        String notes
) {}
