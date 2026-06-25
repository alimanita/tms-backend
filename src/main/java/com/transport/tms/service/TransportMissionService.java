package com.transport.tms.service;

import com.transport.tms.domain.entity.*;
import com.transport.tms.dto.request.TransportMissionRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.TransportMissionResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.TransportMissionMapper;
import com.transport.tms.repository.*;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransportMissionService {

    private final TransportMissionRepository transportMissionRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TransportMissionMapper transportMissionMapper;

    @Transactional(readOnly = true)
    public PageResponse<TransportMissionResponse> list(int page, int size) {
        return PageMapper.map(
                transportMissionRepository.findAllByOrderByDepartureDateDesc(PageRequest.of(page, size)),
                transportMissionMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<TransportMissionResponse> listByDriver(Long driverId, int page, int size) {
        return PageMapper.map(
                transportMissionRepository.findAllByDriver_IdOrderByDepartureDateDesc(driverId, PageRequest.of(page, size)),
                transportMissionMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public java.util.List<Long> findVehicleIdsByDriverId(Long driverId) {
        return transportMissionRepository.findVehicleIdsByDriverId(driverId);
    }

    @Transactional(readOnly = true)
    public TransportMissionResponse getById(Long id) {
        return transportMissionMapper.toResponse(findWithDetails(id));
    }

    @Transactional
    public TransportMissionResponse create(TransportMissionRequest request) {
        if (transportMissionRepository.existsByReference(request.reference())) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference mission deja existante");
        }
        TransportMission mission = applyRequest(new TransportMission(), request);
        return transportMissionMapper.toResponse(transportMissionRepository.save(mission));
    }

    @Transactional
    public TransportMissionResponse update(Long id, TransportMissionRequest request) {
        if (transportMissionRepository.existsByReferenceAndIdNot(request.reference(), id)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference mission deja existante");
        }
        TransportMission mission = findWithDetails(id);
        applyRequest(mission, request);
        return transportMissionMapper.toResponse(transportMissionRepository.save(mission));
    }

    @Transactional
    public void delete(Long id) {
        transportMissionRepository.delete(findWithDetails(id));
    }

    private TransportMission applyRequest(TransportMission mission, TransportMissionRequest request) {
        mission.setReference(request.reference());
        mission.setCustomerOrder(resolveOrder(request.customerOrderId()));
        mission.setCustomer(resolveCustomer(request.customerId()));
        mission.setVehicle(resolveVehicle(request.vehicleId()));
        mission.setDriver(resolveDriver(request.driverId()));
        mission.setDepartureDate(request.departureDate());
        mission.setExpectedArrival(request.expectedArrival());
        mission.setLoadingAddress(request.loadingAddress());
        mission.setDeliveryAddress(request.deliveryAddress());
        mission.setStatus(request.status());
        mission.setRevenue(defaultZero(request.revenue()));
        mission.setTransportCost(defaultZero(request.transportCost()));
        mission.setNotes(request.notes());
        return mission;
    }

    private CustomerOrder resolveOrder(Long id) {
        if (id == null) return null;
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerOrder", id));
    }

    private Customer resolveCustomer(Long id) {
        if (id == null) return null;
        return customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    private Vehicle resolveVehicle(Long id) {
        if (id == null) return null;
        return vehicleRepository.findById(id)
                .filter(Vehicle::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    private Driver resolveDriver(Long id) {
        if (id == null) return null;
        return driverRepository.findById(id)
                .filter(Driver::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private TransportMission findWithDetails(Long id) {
        return transportMissionRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransportMission", id));
    }
}
