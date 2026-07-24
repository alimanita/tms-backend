package com.transport.tms.controller;

import com.transport.tms.dto.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.response.MachineMaintenanceRuleResponse;
import com.transport.tms.service.MachineMaintenanceRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machines/{machineId}/maintenance-rules")
@RequiredArgsConstructor
public class MachineMaintenanceRuleController {

    private final MachineMaintenanceRuleService machineMaintenanceRuleService;

    @GetMapping
    public List<MachineMaintenanceRuleResponse> list(@PathVariable Long machineId) {
        return machineMaintenanceRuleService.listByMachine(machineId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MachineMaintenanceRuleResponse create(
            @PathVariable Long machineId,
            @Valid @RequestBody MachineMaintenanceRuleRequest request) {
        return machineMaintenanceRuleService.create(machineId, request);
    }

    @PutMapping("/{id}")
    public MachineMaintenanceRuleResponse update(
            @PathVariable Long machineId,
            @PathVariable Long id,
            @Valid @RequestBody MachineMaintenanceRuleRequest request) {
        return machineMaintenanceRuleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long machineId, @PathVariable Long id) {
        machineMaintenanceRuleService.delete(id);
    }
}
