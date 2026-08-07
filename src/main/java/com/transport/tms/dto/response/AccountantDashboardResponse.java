package com.transport.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountantDashboardResponse {

    private BigDecimal totalRevenue;
    private BigDecimal estimatedVatOnRevenue; // 20% of revenue

    private BigDecimal totalFuelExpenses;
    private BigDecimal estimatedVatOnFuel; // 20% of fuel

    private BigDecimal totalTollExpenses;
    private BigDecimal estimatedVatOnToll; // 20% of toll

    private BigDecimal totalMaintenanceExpenses;
    private BigDecimal estimatedVatOnMaintenance;

    private BigDecimal netVatToPay; // VAT collected - VAT paid on expenses

    private List<MonthlyVatStat> monthlyStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyVatStat {
        private String month; // YYYY-MM
        private BigDecimal vatCollected;
        private BigDecimal vatPaid;
        private BigDecimal netVat;
    }
}
