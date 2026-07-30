package com.transport.tms.dto.fleet.rapport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarburantAnnuelDto {
    private int annee;
    private BigDecimal litresTotaux;
    private BigDecimal coutTotal;
    private long nombrePleins;
}
