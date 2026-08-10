package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalTvaReportDto {
    private TvaSectionDto tvaCollectee;
    private TvaSectionDto tvaDeductible;
    private NetTvaDto netTva;
    private FiscaliteCarburantDto fiscaliteCarburant;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TvaSectionDto {
        private BigDecimal totalHT;
        private BigDecimal totalTTC;
        private BigDecimal totalTva;
        private List<TvaCategoryDto> categories;
        private List<Map<String, Object>> details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TvaCategoryDto {
        private String name;
        private BigDecimal amountHT;
        private BigDecimal amountTTC;
        private BigDecimal tvaAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetTvaDto {
        private BigDecimal tvaCollectee;
        private BigDecimal tvaDeductible;
        private BigDecimal netAmount; // tvaCollectee - tvaDeductible
        private String status; // A_PAYER ou CREDIT_TVA
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FiscaliteCarburantDto {
        private BigDecimal totalLiters;
        private BigDecimal totalAccise;
        private BigDecimal estimatedReimbursement;
    }
}
