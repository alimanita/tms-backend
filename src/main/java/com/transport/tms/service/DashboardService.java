package com.transport.tms.service;

import com.transport.tms.domain.entity.Notification;
import com.transport.tms.domain.enums.MissionStatus;
import com.transport.tms.domain.enums.VehicleStatus;
import com.transport.tms.dto.response.DashboardResponse;
import com.transport.tms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final FinancialEntryRepository financialEntryRepository;
    private final TransportMissionRepository transportMissionRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        BigDecimal revenue = financialEntryRepository.sumAmountByEntryType("REVENUE");
        BigDecimal expenses = financialEntryRepository.sumAmountByEntryType("EXPENSE");
        BigDecimal netProfit = revenue.subtract(expenses);

        DashboardResponse.KpiResponse kpis = new DashboardResponse.KpiResponse(
                revenue,
                expenses,
                netProfit,
                transportMissionRepository.countByStatusNot(MissionStatus.CANCELLED),
                customerOrderRepository.count(),
                vehicleRepository.countByActiveTrueAndStatus(VehicleStatus.AVAILABLE)
                        + vehicleRepository.countByActiveTrueAndStatus(VehicleStatus.ON_MISSION),
                notificationRepository.countByReadFlagFalse()
        );

        List<Notification> alerts = notificationRepository.findByReadFlagFalseOrderByCreatedAtDesc(PageRequest.of(0, 10));
        List<DashboardResponse.AlertResponse> alertResponses = alerts.stream()
                .map(alert -> new DashboardResponse.AlertResponse(
                        alert.getId(),
                        alert.getType(),
                        alert.getSeverity(),
                        alert.getTitle(),
                        alert.getMessage(),
                        alert.getCreatedAt()
                ))
                .toList();

        LocalDate fromDate = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        List<DashboardResponse.MonthlyAmountResponse> monthlyRevenue =
                aggregateMonthly(financialEntryRepository.sumAmountGroupedByDate("REVENUE", fromDate));
        List<DashboardResponse.MonthlyAmountResponse> monthlyExpenses =
                aggregateMonthly(financialEntryRepository.sumAmountGroupedByDate("EXPENSE", fromDate));

        return new DashboardResponse(kpis, alertResponses, monthlyRevenue, monthlyExpenses);
    }

    private List<DashboardResponse.MonthlyAmountResponse> aggregateMonthly(List<Object[]> rows) {
        Map<String, BigDecimal> grouped = new LinkedHashMap<>();
        YearMonth start = YearMonth.now().minusMonths(5);
        for (int i = 0; i < 6; i++) {
            grouped.put(start.plusMonths(i).format(MONTH_FORMAT), BigDecimal.ZERO);
        }

        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            String key = YearMonth.from(date).format(MONTH_FORMAT);
            grouped.merge(key, amount, BigDecimal::add);
        }

        List<DashboardResponse.MonthlyAmountResponse> result = new ArrayList<>();
        grouped.forEach((month, amount) -> result.add(new DashboardResponse.MonthlyAmountResponse(month, amount)));
        return result;
    }
}
