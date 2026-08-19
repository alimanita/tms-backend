package com.transport.tms.dto.fleet.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OcrTollResult {
    private BigDecimal amountTTC;
    private BigDecimal amountHT;
    private BigDecimal tvaAmount;
    private BigDecimal tvaRate;
    private LocalDateTime receiptDate;
    private String entree;
    private String sortie;
    private String receiptNumber;
    private String operator;
}
