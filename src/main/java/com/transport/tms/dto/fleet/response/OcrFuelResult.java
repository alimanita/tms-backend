package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrFuelResult {
    private BigDecimal quantityLiters;
    private BigDecimal totalCost;
    private BigDecimal tvaAmount;
    private LocalDate fillingDate;
    private String fuelType;
}
