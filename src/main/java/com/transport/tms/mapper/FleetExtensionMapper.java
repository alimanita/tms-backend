package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Machine;
import com.transport.tms.domain.entity.MachineMaintenanceRule;
import com.transport.tms.domain.entity.MissionExpense;
import com.transport.tms.domain.entity.WorkOrder;
import com.transport.tms.dto.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.request.MachineRequest;
import com.transport.tms.dto.request.WorkOrderRequest;
import com.transport.tms.dto.response.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FleetExtensionMapper {

    MissionExpenseResponse toResponse(MissionExpense entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mission", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MissionExpense toEntity(com.transport.tms.dto.request.MissionExpenseRequest request);

    MachineResponse toResponse(Machine entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Machine toEntity(MachineRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MachineRequest request, @MappingTarget Machine entity);

    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineReference", source = "machine.reference")
    MachineMaintenanceRuleResponse toResponse(MachineMaintenanceRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MachineMaintenanceRule toEntity(MachineMaintenanceRuleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "machine", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MachineMaintenanceRuleRequest request, @MappingTarget MachineMaintenanceRule entity);

    @Mapping(target = "entityLabel", ignore = true)
    WorkOrderResponse toResponse(WorkOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PLANNED")
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    WorkOrder toEntity(WorkOrderRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(WorkOrderRequest request, @MappingTarget WorkOrder entity);
}
