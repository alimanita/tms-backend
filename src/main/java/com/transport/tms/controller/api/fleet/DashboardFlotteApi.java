package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.response.DashboardOverviewResponse;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("api/v1/fleet/dashboard")
public interface DashboardFlotteApi {

    @GetMapping("/overview")
    ResponseEntity<DashboardOverviewResponse> getOverview();

    @GetMapping("/top-couts")
    ResponseEntity<List<VehiculeResponse>> getTopVehiculesParCout(
            @RequestParam(defaultValue = "5") int limit);

    @GetMapping("/couts-mensuels")
    ResponseEntity<?> getCoutsMensuels(
            @RequestParam(defaultValue = "12") int mois);

    @GetMapping("/consommation-carburant")
    ResponseEntity<?> getConsommationCarburant(
            @RequestParam(defaultValue = "12") int mois);
}