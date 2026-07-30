package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.fleet.response.MachineMaintenanceRuleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("api/v1/fleet/machine-maintenance-rules")
public interface MachineMaintenanceRuleApi {

    @PostMapping
    ResponseEntity<MachineMaintenanceRuleResponse> create(@Valid @RequestBody MachineMaintenanceRuleRequest request);

    @PutMapping("/{id}")
    ResponseEntity<MachineMaintenanceRuleResponse> update(@PathVariable Long id,
                                                           @Valid @RequestBody MachineMaintenanceRuleRequest request);

    @GetMapping("/{id}")
    ResponseEntity<MachineMaintenanceRuleResponse> findById(@PathVariable Long id);

    @GetMapping("/machine/{machineId}")
    ResponseEntity<List<MachineMaintenanceRuleResponse>> findByMachineId(@PathVariable Long machineId);

    @GetMapping("/actives")
    ResponseEntity<List<MachineMaintenanceRuleResponse>> findAllActives();

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @PatchMapping("/{id}/marquer-effectuee")
    ResponseEntity<MachineMaintenanceRuleResponse> marquerEffectuee(@PathVariable Long id,
                                                                     @RequestParam BigDecimal heuresActuelles);
}