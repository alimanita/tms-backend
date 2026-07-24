package com.transport.tms.service;

import com.transport.tms.domain.entity.Machine;
import com.transport.tms.domain.entity.MachineMaintenanceRule;
import com.transport.tms.dto.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.response.MachineMaintenanceRuleResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FleetExtensionMapper;
import com.transport.tms.repository.MachineMaintenanceRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineMaintenanceRuleService {

    private final MachineMaintenanceRuleRepository ruleRepository;
    private final MachineService machineService;
    private final FleetExtensionMapper fleetExtensionMapper;

    @Transactional(readOnly = true)
    public List<MachineMaintenanceRuleResponse> listByMachine(Long machineId) {
        machineService.getById(machineId);
        return ruleRepository.findAllByMachine_IdAndActiveTrueOrderByCodeAsc(machineId).stream()
                .map(fleetExtensionMapper::toResponse)
                .toList();
    }

    @Transactional
    public MachineMaintenanceRuleResponse create(Long machineId, MachineMaintenanceRuleRequest request) {
        Machine machine = machineService.findActiveEntity(machineId);
        MachineMaintenanceRule rule = fleetExtensionMapper.toEntity(request);
        rule.setMachine(machine);
        if (request.active() != null) {
            rule.setActive(request.active());
        }
        return fleetExtensionMapper.toResponse(ruleRepository.save(rule));
    }

    @Transactional
    public MachineMaintenanceRuleResponse update(Long id, MachineMaintenanceRuleRequest request) {
        MachineMaintenanceRule rule = findRule(id);
        fleetExtensionMapper.updateEntity(request, rule);
        if (request.active() != null) {
            rule.setActive(request.active());
        }
        return fleetExtensionMapper.toResponse(ruleRepository.save(rule));
    }

    @Transactional
    public void delete(Long id) {
        MachineMaintenanceRule rule = findRule(id);
        rule.setActive(false);
        ruleRepository.save(rule);
    }

    private MachineMaintenanceRule findRule(Long id) {
        return ruleRepository.findWithMachineById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MachineMaintenanceRule", id));
    }
}
