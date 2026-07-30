package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Machine;
import com.transport.tms.domain.entity.fleet.MachineMaintenanceRule;
import com.transport.tms.dto.fleet.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.fleet.response.MachineMaintenanceRuleResponse;
import com.transport.tms.mapper.fleet.MachineMaintenanceRuleMapper;
import com.transport.tms.repository.fleet.MachineMaintenanceRuleRepository;
import com.transport.tms.repository.fleet.MachineRepository;
import com.transport.tms.service.fleet.MachineMaintenanceRuleService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MachineMaintenanceRuleServiceImpl implements MachineMaintenanceRuleService {

    private final MachineMaintenanceRuleRepository repository;
    private final MachineRepository machineRepository;
    private final MachineMaintenanceRuleMapper mapper;

    @Override
    public MachineMaintenanceRuleResponse create(MachineMaintenanceRuleRequest request) {
        Machine machine = machineRepository.findById(request.machineId())  // ← record : .machineId()
                .orElseThrow(() -> new RuntimeException("Machine introuvable avec id: " + request.machineId()));

        MachineMaintenanceRule entity = mapper.toEntity(request, machine);
        entity = repository.save(entity);
        return mapper.toResponse(entity);                                   // ← toResponse()
    }

    @Override
    public MachineMaintenanceRuleResponse update(Long id, MachineMaintenanceRuleRequest request) {
        MachineMaintenanceRule entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Règle de maintenance introuvable avec id: " + id));

        Machine machine = null;
        if (request.machineId() != null && !entity.getMachine().getId().equals(request.machineId())) {
            machine = machineRepository.findById(request.machineId())
                    .orElseThrow(() -> new RuntimeException("Machine introuvable avec id: " + request.machineId()));
        }

        mapper.updateEntity(entity, request, machine);                      // ← updateEntity()
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineMaintenanceRuleResponse findById(Long id) {
        MachineMaintenanceRule entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Règle de maintenance introuvable avec id: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineMaintenanceRuleResponse> findByMachineId(Long machineId) {
        return repository.findByMachineId(machineId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineMaintenanceRuleResponse> findAllActives() {
        return repository.findAllActives().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Règle de maintenance introuvable avec id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public MachineMaintenanceRuleResponse marquerEffectuee(Long id, BigDecimal heuresActuelles) {
        MachineMaintenanceRule entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Règle de maintenance introuvable avec id: " + id));

        entity.setDernieresHeuresEffectuees(heuresActuelles);
        entity.setDerniereDateEffectuee(LocalDate.now());
        entity = repository.save(entity);
        return mapper.toResponse(entity);
    }
}