package com.transport.tms.service;

import com.transport.tms.dto.response.AccountantDashboardResponse;

import com.transport.tms.repository.fleet.MissionRepository;
import com.transport.tms.repository.fleet.OrdreTravailRepository;
import com.transport.tms.repository.fleet.PleinCarburantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AccountantDashboardService {

    private final MissionRepository missionRepository;
    private final PleinCarburantRepository pleinCarburantRepository;
    private final OrdreTravailRepository ordreTravailRepository;

    @Transactional(readOnly = true)
    public AccountantDashboardResponse getAccountantDashboard() {
        AccountantDashboardResponse response = new AccountantDashboardResponse();

        // Revenue
        BigDecimal totalRevenue = missionRepository.sumAllRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        response.setTotalRevenue(totalRevenue);
        // Assuming VAT is 20% for revenue
        BigDecimal estVatOnRevenue = totalRevenue.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        response.setEstimatedVatOnRevenue(estVatOnRevenue);

        // Fuel
        BigDecimal totalFuel = pleinCarburantRepository.sumAllCoutCarburant();
        if (totalFuel == null) totalFuel = BigDecimal.ZERO;
        response.setTotalFuelExpenses(totalFuel);
        // Assuming VAT is 20% on fuel
        BigDecimal estVatOnFuel = totalFuel.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        response.setEstimatedVatOnFuel(estVatOnFuel);

        // Toll
        BigDecimal totalToll = missionRepository.sumAllTollCost();
        if (totalToll == null) totalToll = BigDecimal.ZERO;
        response.setTotalTollExpenses(totalToll);
        // Assuming VAT is 20% on toll
        BigDecimal estVatOnToll = totalToll.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        response.setEstimatedVatOnToll(estVatOnToll);

        // Maintenance
        BigDecimal totalMaintenance = ordreTravailRepository.sumAllCout();
        if (totalMaintenance == null) totalMaintenance = BigDecimal.ZERO;
        response.setTotalMaintenanceExpenses(totalMaintenance);
        BigDecimal estVatOnMaintenance = totalMaintenance.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
        response.setEstimatedVatOnMaintenance(estVatOnMaintenance);

        // Net VAT = VAT Collected (Revenue) - VAT Paid (Fuel + Toll + Maintenance)
        BigDecimal vatPaid = estVatOnFuel.add(estVatOnToll).add(estVatOnMaintenance);
        response.setNetVatToPay(estVatOnRevenue.subtract(vatPaid));

        // Leaving monthly stats empty for now
        response.setMonthlyStats(new ArrayList<>());

        return response;
    }
}
