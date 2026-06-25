package com.transport.tms.service;

import com.transport.tms.domain.entity.MaintenanceRecord;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.MaintenanceRecordRequest;
import com.transport.tms.dto.response.MaintenanceRecordResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.MaintenanceRecordMapper;
import com.transport.tms.repository.MaintenanceRecordRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final MaintenanceRecordMapper maintenanceRecordMapper;

    @Transactional(readOnly = true)
    public PageResponse<MaintenanceRecordResponse> list(int page, int size) {
        return PageMapper.map(
                maintenanceRecordRepository.findAllByOrderByMaintenanceDateDesc(PageRequest.of(page, size)),
                maintenanceRecordMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<MaintenanceRecordResponse> listByVehicleIds(java.util.List<Long> vehicleIds, int page, int size) {
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return new PageResponse<>(java.util.List.of(), 0, 0, 0L, 0);
        }
        return PageMapper.map(
                maintenanceRecordRepository.findAllByVehicle_IdInOrderByMaintenanceDateDesc(vehicleIds, PageRequest.of(page, size)),
                maintenanceRecordMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public MaintenanceRecordResponse getById(Long id) {
        return maintenanceRecordMapper.toResponse(findWithDetails(id));
    }

    @Transactional
    public MaintenanceRecordResponse create(MaintenanceRecordRequest request) {
        MaintenanceRecord record = applyRequest(new MaintenanceRecord(), request);
        return maintenanceRecordMapper.toResponse(maintenanceRecordRepository.save(record));
    }

    @Transactional
    public MaintenanceRecordResponse update(Long id, MaintenanceRecordRequest request) {
        MaintenanceRecord record = findWithDetails(id);
        applyRequest(record, request);
        return maintenanceRecordMapper.toResponse(maintenanceRecordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        maintenanceRecordRepository.delete(findWithDetails(id));
    }

    private MaintenanceRecord applyRequest(MaintenanceRecord record, MaintenanceRecordRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
        record.setVehicle(vehicle);
        record.setMaintenanceType(request.maintenanceType());
        record.setMaintenanceDate(request.maintenanceDate());
        record.setMileage(request.mileage());
        record.setCost(request.cost());
        record.setSupplier(request.supplier());
        record.setNextDueDate(request.nextDueDate());
        record.setNextDueMileage(request.nextDueMileage());
        return record;
    }

    private MaintenanceRecord findWithDetails(Long id) {
        return maintenanceRecordRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRecord", id));
    }
}
