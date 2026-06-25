package com.transport.tms.service;

import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.VehicleRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.VehicleResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.VehicleMapper;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public PageResponse<VehicleResponse> list(int page, int size) {
        return PageMapper.map(
                vehicleRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("registration"))),
                vehicleMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        return vehicleMapper.toResponse(findActive(id));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByRegistrationIgnoreCase(request.registration())) {
            throw new BusinessException("DUPLICATE_REGISTRATION", "Immatriculation deja utilisee");
        }
        Vehicle saved = vehicleRepository.save(vehicleMapper.toEntity(request));
        return vehicleMapper.toResponse(saved);
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = findActive(id);
        vehicleMapper.updateEntity(request, vehicle);
        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findActive(id);
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    private Vehicle findActive(Long id) {
        return vehicleRepository.findById(id)
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }
}
