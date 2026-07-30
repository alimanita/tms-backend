package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.PlanMaintenanceApi;
import com.transport.tms.dto.fleet.request.PlanMaintenanceRequest;
import com.transport.tms.dto.fleet.response.PlanMaintenanceResponse;
import com.transport.tms.service.fleet.PlanMaintenanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanMaintenanceController implements PlanMaintenanceApi {

    private final PlanMaintenanceService planMaintenanceService;

    @Override
    public ResponseEntity<Page<PlanMaintenanceResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(planMaintenanceService.findAll(pageable));
    }

    @Override
    public ResponseEntity<PlanMaintenanceResponse> findById(Long id) {
        return ResponseEntity.ok(planMaintenanceService.findById(id));
    }

    @Override
    public ResponseEntity<PlanMaintenanceResponse> create(PlanMaintenanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planMaintenanceService.create(request));
    }

    @Override
    public ResponseEntity<PlanMaintenanceResponse> update(Long id,
                                                          PlanMaintenanceRequest request) {
        return ResponseEntity.ok(planMaintenanceService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        planMaintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PlanMaintenanceResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(planMaintenanceService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<PlanMaintenanceResponse>> findByMachine(Long machineId) {
        return ResponseEntity.ok(planMaintenanceService.findByMachine(machineId));
    }

    @Override
    public ResponseEntity<List<PlanMaintenanceResponse>> findEcheancesProches() {
        return ResponseEntity.ok(planMaintenanceService.findEcheancesProches());
    }
}