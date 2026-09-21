package com.transport.tms.dto.fleet.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatchTicketSaveItem {
    private int ticketIndex;
    private String ticketType; // "PEAGE" ou "CARBURANT"
    
    // Champs communs
    private Long vehiculeId;
    private Long chauffeurId;
    private Long missionId;
    private String operationDate; // ISO: YYYY-MM-DDTHH:mm:ss
    private String receiptNumber;
    private String notes;
    
    // Champs péage
    private BigDecimal amountTTC;
    private BigDecimal amountHT;
    private BigDecimal tvaAmount;
    private BigDecimal tvaRate;
    private String gareEntree;
    private String gareSortie;
    private String societeAutoroute;
    
    // Champs carburant
    private BigDecimal quantityLiters;
    private BigDecimal pricePerLiter;
    private BigDecimal totalCost;
    private String fuelType;
}
