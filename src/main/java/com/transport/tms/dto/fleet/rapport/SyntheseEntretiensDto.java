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
public class SyntheseEntretiensDto {
    private BigDecimal coutTotalMaintenance;
    private BigDecimal coutMainOeuvreTotale;
    private BigDecimal coutPiecesTotales;
    private BigDecimal coutTotalCarburant;
    private BigDecimal coutGlobal;
    private long nombreOT;
    private long nombrePleins;
    private BigDecimal litresTotaux;
}
