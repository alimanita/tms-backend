package com.transport.tms.service;

import com.transport.tms.domain.entity.OilChange;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.OilChangeRequest;
import com.transport.tms.dto.response.OilChangeResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.OilChangeMapper;
import com.transport.tms.repository.OilChangeRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OilChangeService {

    private final OilChangeRepository repository;
    private final OilChangeMapper mapper;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public PageResponse<OilChangeResponse> list(int page, int size) {
        return PageMapper.map(
                repository.findAllByOrderByChangeDateDesc(PageRequest.of(page, size)),
                mapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public OilChangeResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public List<OilChangeResponse> getByVehicle(Long vehicleId) {
        return repository.findByVehicleId(vehicleId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public OilChangeResponse create(OilChangeRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
                
        OilChange entity = mapper.toEntity(request);
        entity.setVehicle(vehicle);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public OilChangeResponse update(Long id, OilChangeRequest request) {
        OilChange entity = findEntity(id);
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
        mapper.updateEntity(request, entity);
        entity.setVehicle(vehicle);
        return mapper.toResponse(repository.save(entity));
    }
    
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("OilChange", id);
        }
        repository.deleteById(id);
    }

    private OilChange findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OilChange", id));
    }
}
