package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.PlanMaintenanceRequest;
import com.transport.tms.dto.fleet.response.PlanMaintenanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/fleet/plans-maintenance")
public interface PlanMaintenanceApi {

    @GetMapping
    ResponseEntity<Page<PlanMaintenanceResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PlanMaintenanceResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<PlanMaintenanceResponse> create(@Valid @RequestBody PlanMaintenanceRequest request);

    @PutMapping("/{id}")
    ResponseEntity<PlanMaintenanceResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody PlanMaintenanceRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<PlanMaintenanceResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/machine/{machineId}")
    ResponseEntity<List<PlanMaintenanceResponse>> findByMachine(@PathVariable Long machineId);

    @GetMapping("/echeances")
    ResponseEntity<List<PlanMaintenanceResponse>> findEcheancesProches();
}