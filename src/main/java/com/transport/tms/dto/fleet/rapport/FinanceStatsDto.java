package com.transport.tms.dto.fleet.rapport;

import lombok.Data;
import java.util.Map;

@Data
public class FinanceStatsDto {
    private Map<String, java.math.BigDecimal> monthlyRevenue;
    private Map<String, java.math.BigDecimal> monthlyExpenses;
    private Map<String, java.math.BigDecimal> monthlyResult;
    private Map<String, java.math.BigDecimal> fleetExpensesByCategory;
}
