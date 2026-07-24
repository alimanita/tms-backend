package com.transport.tms.service;

import com.transport.tms.domain.entity.MaintenancePlan;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.MaintenancePlanRequest;
import com.transport.tms.dto.response.MaintenancePlanResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.MaintenancePlanMapper;
import com.transport.tms.repository.MaintenancePlanRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenancePlanService {

    private final MaintenancePlanRepository repository;
    private final MaintenancePlanMapper mapper;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public PageResponse<MaintenancePlanResponse> list(int page, int size) {
        return PageMapper.map(
                repository.findByActiveTrue(PageRequest.of(page, size, Sort.by("id").descending())),
                mapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public List<MaintenancePlanResponse> getByVehicle(Long vehicleId) {
        return repository.findByVehicleId(vehicleId).stream()
                .filter(MaintenancePlan::isActive)
                .map(mapper::toResponse).toList();
    }

    @Transactional
    public MaintenancePlanResponse create(MaintenancePlanRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
        
        MaintenancePlan entity = mapper.toEntity(request);
        entity.setVehicle(vehicle);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public MaintenancePlanResponse update(Long id, MaintenancePlanRequest request) {
        MaintenancePlan plan = repository.findById(id)
                .filter(MaintenancePlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenancePlan", id));
        mapper.updateEntity(request, plan);
        return mapper.toResponse(repository.save(plan));
    }

    @Transactional
    public void delete(Long id) {
        MaintenancePlan plan = repository.findById(id)
                .filter(MaintenancePlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenancePlan", id));
        plan.setActive(false);
        repository.save(plan);
    }
}
