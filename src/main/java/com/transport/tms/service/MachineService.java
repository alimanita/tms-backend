package com.transport.tms.service;

import com.transport.tms.domain.entity.Machine;
import com.transport.tms.dto.request.MachineRequest;
import com.transport.tms.dto.request.UpdateMachineHoursRequest;
import com.transport.tms.dto.response.MachineResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FleetExtensionMapper;
import com.transport.tms.repository.MachineRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;
    private final FleetExtensionMapper fleetExtensionMapper;

    @Transactional(readOnly = true)
    public PageResponse<MachineResponse> list(int page, int size) {
        return PageMapper.map(
                machineRepository.findByActiveTrueOrderByReferenceAsc(PageRequest.of(page, size)),
                fleetExtensionMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public List<MachineResponse> listActive() {
        return machineRepository.findByActiveTrueOrderByReferenceAsc().stream()
                .map(fleetExtensionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MachineResponse getById(Long id) {
        return fleetExtensionMapper.toResponse(findActive(id));
    }

    @Transactional
    public MachineResponse create(MachineRequest request) {
        if (machineRepository.existsByReferenceIgnoreCase(request.reference())) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference machine deja existante");
        }
        return fleetExtensionMapper.toResponse(machineRepository.save(fleetExtensionMapper.toEntity(request)));
    }

    @Transactional
    public MachineResponse update(Long id, MachineRequest request) {
        if (machineRepository.existsByReferenceIgnoreCaseAndIdNot(request.reference(), id)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference machine deja existante");
        }
        Machine machine = findActive(id);
        fleetExtensionMapper.updateEntity(request, machine);
        return fleetExtensionMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public MachineResponse updateHours(Long id, UpdateMachineHoursRequest request) {
        Machine machine = findActive(id);
        machine.setCurrentHours(request.currentHours());
        return fleetExtensionMapper.toResponse(machineRepository.save(machine));
    }

    @Transactional
    public void delete(Long id) {
        Machine machine = findActive(id);
        machine.setActive(false);
        machineRepository.save(machine);
    }

    Machine findActiveEntity(Long id) {
        return findActive(id);
    }

    private Machine findActive(Long id) {
        return machineRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine", id));
    }
}
