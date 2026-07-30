package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.fleet.response.MachineMaintenanceRuleResponse;

import java.util.List;

public interface MachineMaintenanceRuleService {

    MachineMaintenanceRuleResponse create(MachineMaintenanceRuleRequest request);

    MachineMaintenanceRuleResponse update(Long id, MachineMaintenanceRuleRequest request);

    MachineMaintenanceRuleResponse findById(Long id);

    List<MachineMaintenanceRuleResponse> findByMachineId(Long machineId);

    List<MachineMaintenanceRuleResponse> findAllActives();

    void delete(Long id);

    MachineMaintenanceRuleResponse marquerEffectuee(Long id, java.math.BigDecimal heuresActuelles);
}