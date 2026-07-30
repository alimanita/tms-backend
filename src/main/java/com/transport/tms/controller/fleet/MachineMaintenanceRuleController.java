package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.MachineMaintenanceRuleApi;
import com.transport.tms.dto.fleet.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.fleet.response.MachineMaintenanceRuleResponse;
import com.transport.tms.service.fleet.MachineMaintenanceRuleService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MachineMaintenanceRuleController implements MachineMaintenanceRuleApi {

    private final MachineMaintenanceRuleService service;

    @Override
    public ResponseEntity<MachineMaintenanceRuleResponse> create(MachineMaintenanceRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Override
    public ResponseEntity<MachineMaintenanceRuleResponse> update(Long id, MachineMaintenanceRuleRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Override
    public ResponseEntity<MachineMaintenanceRuleResponse> findById(Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    public ResponseEntity<List<MachineMaintenanceRuleResponse>> findByMachineId(Long machineId) {
        return ResponseEntity.ok(service.findByMachineId(machineId));
    }

    @Override
    public ResponseEntity<List<MachineMaintenanceRuleResponse>> findAllActives() {
        return ResponseEntity.ok(service.findAllActives());
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MachineMaintenanceRuleResponse> marquerEffectuee(Long id, BigDecimal heuresActuelles) {
        return ResponseEntity.ok(service.marquerEffectuee(id, heuresActuelles));
    }
}