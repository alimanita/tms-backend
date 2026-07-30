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
public class MaintenanceMensuelleDto {
    private int annee;
    private int mois;
    private String moisLabel; // "Janvier", "Février", ...
    private BigDecimal coutMainOeuvre;
    private BigDecimal coutPieces;
    private BigDecimal coutTotal;
    private long nombreOT;
}
