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
public class CarburantMensuelDto {
    private int annee;
    private int mois;
    private String moisLabel;
    private BigDecimal litresTotaux;
    private BigDecimal coutTotal;
    private long nombrePleins;
    private BigDecimal consommationMoyenne; // L/100km
}