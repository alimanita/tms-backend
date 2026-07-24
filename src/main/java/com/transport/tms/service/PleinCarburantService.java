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

import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PleinCarburantService {

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
    public PageResponse<FuelRecordResponse> listByChauffeur(Long chauffeurId, int page, int size) {
        return PageMapper.map(
                fuelRecordRepository.findAllByDriver_IdOrderByFillDateDesc(chauffeurId, PageRequest.of(page, size)),
                fuelRecordMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public FuelRecordResponse getById(Long id) {
        return fuelRecordMapper.toResponse(findWithDetails(id));
    }

    @Transactional
    public FuelRecordResponse create(FuelRecordRequest request) {
        FuelRecord plein = applyRequest(new FuelRecord(), request);
        return fuelRecordMapper.toResponse(fuelRecordRepository.save(plein));
    }

    @Transactional
    public FuelRecordResponse update(Long id, FuelRecordRequest request) {
        FuelRecord plein = findWithDetails(id);
        applyRequest(plein, request);
        return fuelRecordMapper.toResponse(fuelRecordRepository.save(plein));
    }

    @Transactional
    public void delete(Long id) {
        fuelRecordRepository.delete(findWithDetails(id));
    }

    private FuelRecord applyRequest(FuelRecord plein, FuelRecordRequest request) {
        Vehicle vehicule = vehicleRepository.findById(request.vehicleId())
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", request.vehicleId()));
        plein.setVehicle(vehicule);
        plein.setDriver(resolveChauffeur(request.driverId()));
        plein.setFillDate(request.fillDate());
        plein.setMileage(request.mileage());
        plein.setStation(request.station());
        plein.setLiters(request.liters());
        plein.setPricePerLiter(request.pricePerLiter());
        plein.setTotalAmount(request.liters().multiply(request.pricePerLiter()).setScale(2, RoundingMode.HALF_UP));
        return plein;
    }

    private Driver resolveChauffeur(Long id) {
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