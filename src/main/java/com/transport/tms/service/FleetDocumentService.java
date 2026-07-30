package com.transport.tms.service;

import com.transport.tms.domain.entity.FleetDocument;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.dto.request.FleetDocumentRequest;
import com.transport.tms.dto.response.FleetDocumentResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FleetDocumentMapper;
import com.transport.tms.repository.FleetDocumentRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FleetDocumentService {

    private final FleetDocumentRepository repository;
    private final FleetDocumentMapper mapper;
    private final VehiculeRepository vehicleRepository;
    private final ChauffeurRepository driverRepository;

    @Transactional(readOnly = true)
    public PageResponse<FleetDocumentResponse> list(int page, int size) {
        return PageMapper.map(
                repository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)),
                mapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public FleetDocumentResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Transactional(readOnly = true)
    public List<FleetDocumentResponse> getByVehicle(Long vehicleId) {
        return repository.findByVehicleId(vehicleId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FleetDocumentResponse> getByDriver(Long driverId) {
        return repository.findByDriverId(driverId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public FleetDocumentResponse create(FleetDocumentRequest request) {
        FleetDocument entity = mapper.toEntity(request);
        applyRelations(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    public FleetDocumentResponse update(Long id, FleetDocumentRequest request) {
        FleetDocument entity = findEntity(id);
        mapper.updateEntity(request, entity);
        applyRelations(request, entity);
        return mapper.toResponse(repository.save(entity));
    }
    
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("FleetDocument", id);
        }
        repository.deleteById(id);
    }

    private FleetDocument findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FleetDocument", id));
    }

    private void applyRelations(FleetDocumentRequest request, FleetDocument entity) {
        if (request.vehicleId() != null) {
            Vehicule vehicle = vehicleRepository.findById(request.vehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
            entity.setVehicle(vehicle);
            entity.setDriver(null);
        } else if (request.driverId() != null) {
            Chauffeur driver = driverRepository.findById(request.driverId())
                    .orElseThrow();
            entity.setDriver(driver);
            entity.setVehicle(null);
        } else {
            throw new BusinessException("INVALID_REQUEST", "Either vehicleId or driverId must be provided");
        }
    }
}
