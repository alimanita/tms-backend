package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchTicketItemResult {
    private int ticketIndex;
    private String fileName;
    
    // Classification
    private String ticketType;       // "PEAGE", "CARBURANT", "UNKNOWN"
    private String typeConfidence;   // "HIGH", "LOW", "UNKNOWN"
    
    // Statut traitement
    private String processingStatus; // "OK", "PARTIAL", "ERROR"
    private String errorMessage;
    
    // Date
    private String operationDate;    // format YYYY-MM-DDTHH:mm:ss
    private String dateConfidence;   // "HIGH", "LOW", "NONE"
    private String dateWarning;
    
    // Champs péage
    private BigDecimal amountTTC;
    private BigDecimal amountHT;
    private BigDecimal tvaAmount;
    private BigDecimal tvaRate;
    private String gareEntree;
    private String gareSortie;
    private String receiptNumber;
    private String societeAutoroute;
    
    // Champs carburant
    private BigDecimal quantityLiters;
    private BigDecimal totalCost;
    private BigDecimal pricePerLiter;
    private String fuelType;
}
