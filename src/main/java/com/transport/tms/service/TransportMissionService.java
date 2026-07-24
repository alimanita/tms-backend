package com.transport.tms.service;

import com.transport.tms.domain.entity.*;
import com.transport.tms.domain.enums.MissionStatus;
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
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransportMissionService {

    private final TransportMissionRepository transportMissionRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TransportMissionMapper transportMissionMapper;
    private final MissionExpenseService missionExpenseService;

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
        return toEnrichedResponse(findWithDetails(id));
    }

    @Transactional
    public TransportMissionResponse create(TransportMissionRequest request) {
        String reference = (request.reference() == null || request.reference().isBlank())
                ? generateReference()
                : request.reference();

        if (transportMissionRepository.existsByReference(reference)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference mission deja existante");
        }

        TransportMission mission = applyRequest(new TransportMission(), request);
        mission.setReference(reference);
        return toEnrichedResponse(transportMissionRepository.save(mission));
    }

    private String generateReference() {
        int year = java.time.Year.now().getValue();
        String prefix = "MIS-" + year + "-";
        long count = transportMissionRepository.countByReferenceStartingWith(prefix);
        return prefix + String.format("%04d", count + 1);
    }

    @Transactional
    public TransportMissionResponse update(Long id, TransportMissionRequest request) {
        if (transportMissionRepository.existsByReferenceAndIdNot(request.reference(), id)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference mission deja existante");
        }
        TransportMission mission = findWithDetails(id);
        applyRequest(mission, request);
        return toEnrichedResponse(transportMissionRepository.save(mission));
    }

    @Transactional
    public void delete(Long id) {
        transportMissionRepository.delete(findWithDetails(id));
    }

    @Transactional
    public TransportMissionResponse start(Long id) {
        TransportMission mission = findWithDetails(id);
        if (mission.getStatus() != MissionStatus.PLANNED && mission.getStatus() != MissionStatus.ASSIGNED) {
            throw new BusinessException("INVALID_STATUS", "La mission doit etre planifiee ou affectee pour demarrer");
        }
        mission.setStatus(MissionStatus.IN_PROGRESS);
        if (mission.getDepartureDate() == null) {
            mission.setDepartureDate(Instant.now());
        }
        return toEnrichedResponse(transportMissionRepository.save(mission));
    }

    @Transactional
    public TransportMissionResponse complete(Long id) {
        TransportMission mission = findWithDetails(id);
        if (mission.getStatus() != MissionStatus.IN_PROGRESS) {
            throw new BusinessException("INVALID_STATUS", "La mission doit etre en cours pour etre cloturee");
        }
        mission.setStatus(MissionStatus.DELIVERED);
        mission.setActualArrival(Instant.now());
        return toEnrichedResponse(transportMissionRepository.save(mission));
    }

    @Transactional
    public TransportMissionResponse cancel(Long id, String reason) {
        TransportMission mission = findWithDetails(id);
        if (mission.getStatus() == MissionStatus.DELIVERED || mission.getStatus() == MissionStatus.CANCELLED) {
            throw new BusinessException("INVALID_STATUS", "Cette mission ne peut plus etre annulee");
        }
        mission.setStatus(MissionStatus.CANCELLED);
        mission.setCancellationReason(reason);
        return toEnrichedResponse(transportMissionRepository.save(mission));
    }

    private TransportMissionResponse toEnrichedResponse(TransportMission mission) {
        TransportMissionResponse base = transportMissionMapper.toResponse(mission);
        return new TransportMissionResponse(
                base.id(), base.reference(), base.customerOrderId(), base.customerOrderReference(),
                base.customerId(), base.customerName(), base.vehicleId(), base.vehicleRegistration(),
                base.driverId(), base.driverName(), base.departureDate(), base.expectedArrival(),
                base.actualArrival(), base.loadingAddress(), base.deliveryAddress(), base.status(),
                base.revenue(), base.transportCost(), missionExpenseService.totalByMission(mission.getId()),
                base.notes(), mission.getCancellationReason()
        );
    }

    private TransportMission applyRequest(TransportMission mission, TransportMissionRequest request) {
//mission.setReference(request.reference());
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
