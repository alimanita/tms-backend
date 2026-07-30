package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.DashboardFlotteApi;
import com.transport.tms.dto.fleet.response.DashboardOverviewResponse;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import com.transport.tms.service.fleet.DashboardFlotteService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DashboardFlotteController implements DashboardFlotteApi {

    private final DashboardFlotteService dashboardService;

    @Override
    public ResponseEntity<DashboardOverviewResponse> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }

    @Override
    public ResponseEntity<List<VehiculeResponse>> getTopVehiculesParCout(int limit) {
        return ResponseEntity.ok(dashboardService.getTopVehiculesParCout(limit));
    }

    @Override
    public ResponseEntity<?> getCoutsMensuels(int mois) {
        return ResponseEntity.ok(dashboardService.getCoutsMensuels(mois));
    }

    @Override
    public ResponseEntity<?> getConsommationCarburant(int mois) {
        return ResponseEntity.ok(dashboardService.getConsommationCarburant(mois));
    }
}