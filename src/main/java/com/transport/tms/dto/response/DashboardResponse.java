package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        KpiResponse kpis,
        List<AlertResponse> alerts,
        List<MonthlyAmountResponse> monthlyRevenue,
        List<MonthlyAmountResponse> monthlyExpenses
) {
    public record KpiResponse(
            BigDecimal revenue,
            BigDecimal expenses,
            BigDecimal netProfit,
            long missionCount,
            long orderCount,
            long activeVehicleCount,
            long unreadAlerts
    ) {}

    public record AlertResponse(
            Long id,
            String type,
            String severity,
            String title,
            String message,
            Instant createdAt
    ) {}

    public record MonthlyAmountResponse(
            String month,
            BigDecimal amount
    ) {}
}
