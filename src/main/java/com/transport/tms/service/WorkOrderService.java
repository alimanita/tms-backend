package com.transport.tms.service;

import com.transport.tms.domain.entity.Machine;
import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.entity.WorkOrder;
import com.transport.tms.domain.enums.WorkOrderEntityType;
import com.transport.tms.domain.enums.WorkOrderStatus;
import com.transport.tms.dto.request.WorkOrderRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.WorkOrderResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FleetExtensionMapper;
import com.transport.tms.repository.MachineRepository;
import com.transport.tms.repository.VehicleRepository;
import com.transport.tms.repository.WorkOrderRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final MachineRepository machineRepository;
    private final FleetExtensionMapper fleetExtensionMapper;

    @Transactional(readOnly = true)
    public PageResponse<WorkOrderResponse> list(int page, int size, WorkOrderStatus status) {
        var pageable = PageRequest.of(page, size);
        var data = status == null
                ? workOrderRepository.findAllByOrderByCreatedAtDesc(pageable)
                : workOrderRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable);
        return PageMapper.map(data, this::toResponse);
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> listByEntity(WorkOrderEntityType entityType, Long entityId) {
        return workOrderRepository.findAllByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkOrderResponse create(WorkOrderRequest request) {

        validateEntity(request.entityType(), request.entityId());
        WorkOrder workOrder = fleetExtensionMapper.toEntity(request);
        applyDefaults(workOrder, request);
        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse update(Long id, WorkOrderRequest request) {

        WorkOrder workOrder = findById(id);
        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new BusinessException("WORK_ORDER_CLOSED", "Cet ordre de travail ne peut plus etre modifie");
        }
        validateEntity(request.entityType(), request.entityId());
        fleetExtensionMapper.updateEntity(request, workOrder);
        applyDefaults(workOrder, request);
        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse start(Long id) {
        WorkOrder workOrder = findById(id);
        if (workOrder.getStatus() != WorkOrderStatus.PLANNED) {
            throw new BusinessException("INVALID_STATUS", "L'OT doit etre PLANNED pour demarrer");
        }
        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.setStartedAt(Instant.now());
        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse complete(Long id) {
        WorkOrder workOrder = findById(id);
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessException("INVALID_STATUS", "L'OT doit etre IN_PROGRESS pour cloturer");
        }
        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setCompletedAt(Instant.now());
        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public WorkOrderResponse cancel(Long id) {
        WorkOrder workOrder = findById(id);
        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED) {
            throw new BusinessException("INVALID_STATUS", "Un OT complete ne peut pas etre annule");
        }
        workOrder.setStatus(WorkOrderStatus.CANCELLED);
        return toResponse(workOrderRepository.save(workOrder));
    }

    @Transactional
    public void delete(Long id) {
        workOrderRepository.delete(findById(id));
    }

    private void applyDefaults(WorkOrder workOrder, WorkOrderRequest request) {
        if (request.priority() != null) {
            workOrder.setPriority(request.priority());
        }
        if (request.actualCost() != null) {
            workOrder.setActualCost(request.actualCost());
        } else if (workOrder.getActualCost() == null) {
            workOrder.setActualCost(BigDecimal.ZERO);
        }
    }

    private void validateEntity(WorkOrderEntityType entityType, Long entityId) {
        if (entityType == WorkOrderEntityType.VEHICLE) {
            vehicleRepository.findById(entityId)
                    .filter(Vehicle::isActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", entityId));
        } else {
            machineRepository.findByIdAndActiveTrue(entityId)
                    .orElseThrow(() -> new ResourceNotFoundException("Machine", entityId));
        }
    }

    private WorkOrder findById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", id));
    }

    private WorkOrderResponse toResponse(WorkOrder entity) {
        WorkOrderResponse base = fleetExtensionMapper.toResponse(entity);
        return new WorkOrderResponse(
                base.id(), base.reference(), base.entityType(), base.entityId(),
                resolveEntityLabel(entity.getEntityType(), entity.getEntityId()),
                base.orderType(), base.priority(), base.status(), base.maintenanceType(),
                base.description(), base.scheduledDate(), base.startedAt(), base.completedAt(),
                base.mileageAtOrder(), base.hoursAtOrder(), base.estimatedCost(), base.actualCost(),
                base.notes(), base.createdAt()
        );
    }

    private String resolveEntityLabel(WorkOrderEntityType entityType, Long entityId) {
        if (entityType == WorkOrderEntityType.VEHICLE) {
            return vehicleRepository.findById(entityId)
                    .map(Vehicle::getRegistration)
                    .orElse("Vehicule #" + entityId);
        }
        return machineRepository.findById(entityId)
                .map(Machine::getReference)
                .orElse("Machine #" + entityId);
    }
}
