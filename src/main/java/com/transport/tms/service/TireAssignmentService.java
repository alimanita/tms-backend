package com.transport.tms.service;

import com.transport.tms.domain.entity.TireAssignment;
import com.transport.tms.domain.entity.Tire;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.TireAssignmentRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.TireAssignmentResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.TireAssignmentMapper;
import com.transport.tms.repository.TireAssignmentRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TireAssignmentService {

    private final TireAssignmentRepository repository;
    private final TireAssignmentMapper mapper;
    private final TireService tireService;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public PageResponse<TireAssignmentResponse> list(int page, int size) {
        return PageMapper.map(
                repository.findAllByOrderByMountDateDesc(PageRequest.of(page, size)),
                mapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public List<TireAssignmentResponse> getByVehicle(Long vehicleId) {
        return repository.findByVehicleId(vehicleId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TireAssignmentResponse> getByTire(Long tireId) {
        return repository.findByTireId(tireId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public TireAssignmentResponse create(TireAssignmentRequest request) {
        Tire tire = tireService.findActive(request.tireId());
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
                
        TireAssignment entity = mapper.toEntity(request);
        entity.setTire(tire);
        entity.setVehicle(vehicle);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public void unmount(Long id, TireAssignmentRequest request) {
        TireAssignment assignment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TireAssignment", id));
        assignment.setUnmountDate(request.unmountDate());
        assignment.setUnmountMileage(request.unmountMileage());
        assignment.setReasonUnmount(request.reasonUnmount());
        repository.save(assignment);
    }
}
