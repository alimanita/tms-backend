package com.transport.tms.service;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.domain.entity.FuelRecord;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.FuelRecordRequest;
import com.transport.tms.dto.response.FuelRecordResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FuelRecordMapper;
import com.transport.tms.repository.DriverRepository;
import com.transport.tms.repository.FuelRecordRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class FuelRecordService {

    private final FuelRecordRepository fuelRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final FuelRecordMapper fuelRecordMapper;

    @Transactional(readOnly = true)
    public PageResponse<FuelRecordResponse> list(int page, int size) {
        return PageMapper.map(
                fuelRecordRepository.findAllByOrderByFillDateDesc(PageRequest.of(page, size)),
                fuelRecordMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<FuelRecordResponse> listByDriver(Long driverId, int page, int size) {
        return PageMapper.map(
                fuelRecordRepository.findAllByDriver_IdOrderByFillDateDesc(driverId, PageRequest.of(page, size)),
                fuelRecordMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public FuelRecordResponse getById(Long id) {
        return fuelRecordMapper.toResponse(findWithDetails(id));
    }

    @Transactional
    public FuelRecordResponse create(FuelRecordRequest request) {
        FuelRecord record = applyRequest(new FuelRecord(), request);
        return fuelRecordMapper.toResponse(fuelRecordRepository.save(record));
    }

    @Transactional
    public FuelRecordResponse update(Long id, FuelRecordRequest request) {
        FuelRecord record = findWithDetails(id);
        applyRequest(record, request);
        return fuelRecordMapper.toResponse(fuelRecordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        fuelRecordRepository.delete(findWithDetails(id));
    }

    private FuelRecord applyRequest(FuelRecord record, FuelRecordRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
        record.setVehicle(vehicle);
        record.setDriver(resolveDriver(request.driverId()));
        record.setFillDate(request.fillDate());
        record.setMileage(request.mileage());
        record.setStation(request.station());
        record.setLiters(request.liters());
        record.setPricePerLiter(request.pricePerLiter());
        record.setTotalAmount(request.liters().multiply(request.pricePerLiter()).setScale(2, RoundingMode.HALF_UP));
        return record;
    }

    private Driver resolveDriver(Long id) {
        if (id == null) return null;
        return driverRepository.findById(id)
                .filter(Driver::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
    }

    private FuelRecord findWithDetails(Long id) {
        return fuelRecordRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FuelRecord", id));
    }
}
