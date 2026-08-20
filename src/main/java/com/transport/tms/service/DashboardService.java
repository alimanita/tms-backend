package com.transport.tms.service;

import com.transport.tms.domain.entity.Notification;
import com.transport.tms.dto.response.DashboardResponse;
import com.transport.tms.repository.*;
import com.transport.tms.repository.fleet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private final MissionRepository          missionRepository;
    private final FinancialEntryRepository   financialEntryRepository;
    private final CustomerOrderRepository    customerOrderRepository;
    private final VehiculeRepository         vehicleRepository;
    private final NotificationRepository     notificationRepository;
    private final PleinCarburantRepository   pleinCarburantRepository;
    private final OrdreTravailRepository     ordreTravailRepository;
    private final PeageRepository            peageRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {

        // ── Chiffre d'affaires = somme des revenus des missions COMPLETED ──────
        BigDecimal revenue = missionRepository.sumAllRevenue();
        if (revenue == null) revenue = BigDecimal.ZERO;

        // ── Dépenses = coûts missions + carburant + maintenance ──────────────
        BigDecimal missionCost = missionRepository.sumAllMissionCost();
        BigDecimal missionFuelCost = missionRepository.sumAllFuelCost();
        BigDecimal missionTollCost = missionRepository.sumAllTollCost();
        BigDecimal fuelCost = pleinCarburantRepository.sumAllCoutCarburant();
        BigDecimal peageCost = peageRepository.sumAllCoutPeage();
        BigDecimal maintenanceCost = ordreTravailRepository.sumAllCout();
        
        if (missionCost == null) missionCost = BigDecimal.ZERO;
        if (missionFuelCost == null) missionFuelCost = BigDecimal.ZERO;
        if (missionTollCost == null) missionTollCost = BigDecimal.ZERO;
        if (fuelCost == null) fuelCost = BigDecimal.ZERO;
        if (peageCost == null) peageCost = BigDecimal.ZERO;
        if (maintenanceCost == null) maintenanceCost = BigDecimal.ZERO;
        
        BigDecimal expenses = missionCost.subtract(missionFuelCost).subtract(missionTollCost).add(fuelCost).add(peageCost).add(maintenanceCost);

        // ── Bénéfice net ────────────────────────────────────────────────────────
        BigDecimal netProfit = revenue.subtract(expenses);

        DashboardResponse.KpiResponse kpis = new DashboardResponse.KpiResponse(
                revenue,
                expenses,
                netProfit,
                missionRepository.count(),
                customerOrderRepository.count(),
                vehicleRepository.countByActifTrue(),
                notificationRepository.countByReadFlagFalse()
        );

        // ── Alertes récentes ────────────────────────────────────────────────────
        List<Notification> alerts = notificationRepository
                .findByReadFlagFalseOrderByCreatedAtDesc(PageRequest.of(0, 10));
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

        // ── Graphique mensuel (12 derniers mois) ─────────────────────────────────
        LocalDateTime fromDate = LocalDate.now().minusMonths(11).withDayOfMonth(1).atStartOfDay();

        // Revenus mensuels depuis les missions
        List<DashboardResponse.MonthlyAmountResponse> monthlyRevenue =
                aggregateMissionMonthly(missionRepository.sumRevenueByYearMonth(fromDate));

        // Dépenses mensuelles
        Map<String, BigDecimal> expensesMap = new LinkedHashMap<>();
        // Initialiser les 12 mois à zéro
        YearMonth start = YearMonth.now().minusMonths(11);
        for (int i = 0; i < 12; i++) {
            expensesMap.put(start.plusMonths(i).format(MONTH_FORMAT), BigDecimal.ZERO);
        }
        
        // Ajouter les coûts missions par mois
        for (Object[] row : missionRepository.sumCostByYearMonth(fromDate)) {
            String key = yearMonthKey(row);
            BigDecimal val = toBigDecimal(row[2]);
            expensesMap.merge(key, val, BigDecimal::add);
        }
        // Ajouter les coûts de carburant par mois
        for (Object[] row : pleinCarburantRepository.sumCostByYearMonth(fromDate)) {
            String key = yearMonthKey(row);
            BigDecimal val = toBigDecimal(row[2]);
            expensesMap.merge(key, val, BigDecimal::add);
        }
        // Ajouter les coûts de maintenance par mois
        for (Object[] row : ordreTravailRepository.sumCostByYearMonth(fromDate)) {
            String key = yearMonthKey(row);
            BigDecimal val = toBigDecimal(row[2]);
            expensesMap.merge(key, val, BigDecimal::add);
        }
        // Ajouter les coûts de péages par mois
        for (Object[] row : peageRepository.sumCostByYearMonth(fromDate)) {
            String key = yearMonthKey(row);
            BigDecimal val = toBigDecimal(row[2]);
            expensesMap.merge(key, val, BigDecimal::add);
        }
        
        List<DashboardResponse.MonthlyAmountResponse> monthlyExpenses = new ArrayList<>();
        expensesMap.forEach((month, amount) ->
                monthlyExpenses.add(new DashboardResponse.MonthlyAmountResponse(month, amount)));

        return new DashboardResponse(kpis, alertResponses, monthlyRevenue, monthlyExpenses);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    /** Agrège les lignes [year, month, sum] renvoyées par les requêtes mission. */
    private List<DashboardResponse.MonthlyAmountResponse> aggregateMissionMonthly(List<Object[]> rows) {
        Map<String, BigDecimal> grouped = new LinkedHashMap<>();
        YearMonth start = YearMonth.now().minusMonths(11);
        for (int i = 0; i < 12; i++) {
            grouped.put(start.plusMonths(i).format(MONTH_FORMAT), BigDecimal.ZERO);
        }
        for (Object[] row : rows) {
            String key = yearMonthKey(row);
            BigDecimal val = toBigDecimal(row[2]);
            grouped.merge(key, val, BigDecimal::add);
        }
        List<DashboardResponse.MonthlyAmountResponse> result = new ArrayList<>();
        grouped.forEach((month, amount) ->
                result.add(new DashboardResponse.MonthlyAmountResponse(month, amount)));
        return result;
    }

    /** Construit une clé "yyyy-MM" à partir d'une ligne [year, month, ...]. */
    private String yearMonthKey(Object[] row) {
        int year  = ((Number) row[0]).intValue();
        int month = ((Number) row[1]).intValue();
        return YearMonth.of(year, month).format(MONTH_FORMAT);
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        return new BigDecimal(obj.toString());
    }
}
