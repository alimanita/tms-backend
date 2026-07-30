package com.transport.tms.service.fleet;



import com.transport.tms.dto.fleet.response.DashboardOverviewResponse;
import com.transport.tms.dto.fleet.response.VehiculeResponse;

import java.util.List;
import java.util.Map;

public interface DashboardFlotteService {

    DashboardOverviewResponse getOverview();

    List<VehiculeResponse> getTopVehiculesParCout(int limit);

    Map<String, Object> getCoutsMensuels(int mois);

    Map<String, Object> getConsommationCarburant(int mois);
}