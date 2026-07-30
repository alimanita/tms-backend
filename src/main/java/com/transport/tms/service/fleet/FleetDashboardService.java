package com.transport.tms.service.fleet;



import com.transport.tms.dto.fleet.response.DashboardOverviewResponse;
import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FleetDashboardService {

    DashboardOverviewResponse getOverview();

    Map<String, Object> getVehicleStatusBreakdown();

    Map<String, Object> getMonthlyCosts(LocalDate start, LocalDate end);

    Map<String, Object> getFuelConsumption(LocalDate start, LocalDate end);

    Map<String, Object> getCostPerKm();

    Map<String, Object> getTopMaintenanceCosts(int limit);

    Map<String, Object> getMissionsSummary();

    List<NotificationFlotteResponse> getActiveAlerts();
}