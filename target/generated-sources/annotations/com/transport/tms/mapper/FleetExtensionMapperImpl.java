package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Machine;
import com.transport.tms.domain.entity.MachineMaintenanceRule;
import com.transport.tms.domain.entity.MissionExpense;
import com.transport.tms.domain.entity.WorkOrder;
import com.transport.tms.domain.enums.MachineStatus;
import com.transport.tms.domain.enums.MissionExpenseType;
import com.transport.tms.domain.enums.WorkOrderEntityType;
import com.transport.tms.domain.enums.WorkOrderPriority;
import com.transport.tms.domain.enums.WorkOrderStatus;
import com.transport.tms.domain.enums.WorkOrderType;
import com.transport.tms.dto.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.request.MachineRequest;
import com.transport.tms.dto.request.MissionExpenseRequest;
import com.transport.tms.dto.request.WorkOrderRequest;
import com.transport.tms.dto.response.MachineMaintenanceRuleResponse;
import com.transport.tms.dto.response.MachineResponse;
import com.transport.tms.dto.response.MissionExpenseResponse;
import com.transport.tms.dto.response.WorkOrderResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class FleetExtensionMapperImpl implements FleetExtensionMapper {

    @Override
    public MissionExpenseResponse toResponse(MissionExpense entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        MissionExpenseType expenseType = null;
        BigDecimal amount = null;
        String currency = null;
        Instant expenseDate = null;
        String description = null;
        boolean reimbursable = false;

        id = entity.getId();
        expenseType = entity.getExpenseType();
        amount = entity.getAmount();
        currency = entity.getCurrency();
        expenseDate = entity.getExpenseDate();
        description = entity.getDescription();
        reimbursable = entity.isReimbursable();

        Long missionId = null;

        MissionExpenseResponse missionExpenseResponse = new MissionExpenseResponse( id, missionId, expenseType, amount, currency, expenseDate, description, reimbursable );

        return missionExpenseResponse;
    }

    @Override
    public MissionExpense toEntity(MissionExpenseRequest request) {
        if ( request == null ) {
            return null;
        }

        MissionExpense.MissionExpenseBuilder missionExpense = MissionExpense.builder();

        missionExpense.expenseType( request.expenseType() );
        missionExpense.amount( request.amount() );
        missionExpense.currency( request.currency() );
        missionExpense.expenseDate( request.expenseDate() );
        missionExpense.description( request.description() );
        if ( request.reimbursable() != null ) {
            missionExpense.reimbursable( request.reimbursable() );
        }

        return missionExpense.build();
    }

    @Override
    public MachineResponse toResponse(Machine entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String reference = null;
        String serialNumber = null;
        String name = null;
        String brand = null;
        String model = null;
        String category = null;
        LocalDate purchaseDate = null;
        BigDecimal purchasePrice = null;
        String powerUnit = null;
        BigDecimal powerValue = null;
        BigDecimal initialHours = null;
        BigDecimal currentHours = null;
        String location = null;
        MachineStatus status = null;
        String notes = null;
        boolean active = false;
        Instant createdAt = null;

        id = entity.getId();
        reference = entity.getReference();
        serialNumber = entity.getSerialNumber();
        name = entity.getName();
        brand = entity.getBrand();
        model = entity.getModel();
        category = entity.getCategory();
        purchaseDate = entity.getPurchaseDate();
        purchasePrice = entity.getPurchasePrice();
        powerUnit = entity.getPowerUnit();
        powerValue = entity.getPowerValue();
        initialHours = entity.getInitialHours();
        currentHours = entity.getCurrentHours();
        location = entity.getLocation();
        status = entity.getStatus();
        notes = entity.getNotes();
        active = entity.isActive();
        createdAt = entity.getCreatedAt();

        MachineResponse machineResponse = new MachineResponse( id, reference, serialNumber, name, brand, model, category, purchaseDate, purchasePrice, powerUnit, powerValue, initialHours, currentHours, location, status, notes, active, createdAt );

        return machineResponse;
    }

    @Override
    public Machine toEntity(MachineRequest request) {
        if ( request == null ) {
            return null;
        }

        Machine.MachineBuilder machine = Machine.builder();

        machine.reference( request.reference() );
        machine.serialNumber( request.serialNumber() );
        machine.name( request.name() );
        machine.brand( request.brand() );
        machine.model( request.model() );
        machine.category( request.category() );
        machine.purchaseDate( request.purchaseDate() );
        machine.purchasePrice( request.purchasePrice() );
        machine.powerUnit( request.powerUnit() );
        machine.powerValue( request.powerValue() );
        machine.initialHours( request.initialHours() );
        machine.currentHours( request.currentHours() );
        machine.location( request.location() );
        machine.status( request.status() );
        machine.notes( request.notes() );

        machine.active( true );

        return machine.build();
    }

    @Override
    public void updateEntity(MachineRequest request, Machine entity) {
        if ( request == null ) {
            return;
        }

        if ( request.reference() != null ) {
            entity.setReference( request.reference() );
        }
        if ( request.serialNumber() != null ) {
            entity.setSerialNumber( request.serialNumber() );
        }
        if ( request.name() != null ) {
            entity.setName( request.name() );
        }
        if ( request.brand() != null ) {
            entity.setBrand( request.brand() );
        }
        if ( request.model() != null ) {
            entity.setModel( request.model() );
        }
        if ( request.category() != null ) {
            entity.setCategory( request.category() );
        }
        if ( request.purchaseDate() != null ) {
            entity.setPurchaseDate( request.purchaseDate() );
        }
        if ( request.purchasePrice() != null ) {
            entity.setPurchasePrice( request.purchasePrice() );
        }
        if ( request.powerUnit() != null ) {
            entity.setPowerUnit( request.powerUnit() );
        }
        if ( request.powerValue() != null ) {
            entity.setPowerValue( request.powerValue() );
        }
        if ( request.initialHours() != null ) {
            entity.setInitialHours( request.initialHours() );
        }
        if ( request.currentHours() != null ) {
            entity.setCurrentHours( request.currentHours() );
        }
        if ( request.location() != null ) {
            entity.setLocation( request.location() );
        }
        if ( request.status() != null ) {
            entity.setStatus( request.status() );
        }
        if ( request.notes() != null ) {
            entity.setNotes( request.notes() );
        }
    }

    @Override
    public MachineMaintenanceRuleResponse toResponse(MachineMaintenanceRule entity) {
        if ( entity == null ) {
            return null;
        }

        Long machineId = null;
        String machineReference = null;
        Long id = null;
        String code = null;
        String description = null;
        String actionType = null;
        Integer intervalHours = null;
        Integer intervalDays = null;
        String consumable = null;
        BigDecimal quantity = null;
        String quantityUnit = null;
        BigDecimal lastPerformedHours = null;
        LocalDate lastPerformedDate = null;
        boolean active = false;
        Instant createdAt = null;

        machineId = entityMachineId( entity );
        machineReference = entityMachineReference( entity );
        id = entity.getId();
        code = entity.getCode();
        description = entity.getDescription();
        actionType = entity.getActionType();
        intervalHours = entity.getIntervalHours();
        intervalDays = entity.getIntervalDays();
        consumable = entity.getConsumable();
        quantity = entity.getQuantity();
        quantityUnit = entity.getQuantityUnit();
        lastPerformedHours = entity.getLastPerformedHours();
        lastPerformedDate = entity.getLastPerformedDate();
        active = entity.isActive();
        createdAt = entity.getCreatedAt();

        MachineMaintenanceRuleResponse machineMaintenanceRuleResponse = new MachineMaintenanceRuleResponse( id, machineId, machineReference, code, description, actionType, intervalHours, intervalDays, consumable, quantity, quantityUnit, lastPerformedHours, lastPerformedDate, active, createdAt );

        return machineMaintenanceRuleResponse;
    }

    @Override
    public MachineMaintenanceRule toEntity(MachineMaintenanceRuleRequest request) {
        if ( request == null ) {
            return null;
        }

        MachineMaintenanceRule.MachineMaintenanceRuleBuilder machineMaintenanceRule = MachineMaintenanceRule.builder();

        machineMaintenanceRule.code( request.code() );
        machineMaintenanceRule.description( request.description() );
        machineMaintenanceRule.actionType( request.actionType() );
        machineMaintenanceRule.intervalHours( request.intervalHours() );
        machineMaintenanceRule.intervalDays( request.intervalDays() );
        machineMaintenanceRule.consumable( request.consumable() );
        machineMaintenanceRule.quantity( request.quantity() );
        machineMaintenanceRule.quantityUnit( request.quantityUnit() );
        machineMaintenanceRule.lastPerformedHours( request.lastPerformedHours() );
        machineMaintenanceRule.lastPerformedDate( request.lastPerformedDate() );
        if ( request.active() != null ) {
            machineMaintenanceRule.active( request.active() );
        }

        return machineMaintenanceRule.build();
    }

    @Override
    public void updateEntity(MachineMaintenanceRuleRequest request, MachineMaintenanceRule entity) {
        if ( request == null ) {
            return;
        }

        if ( request.code() != null ) {
            entity.setCode( request.code() );
        }
        if ( request.description() != null ) {
            entity.setDescription( request.description() );
        }
        if ( request.actionType() != null ) {
            entity.setActionType( request.actionType() );
        }
        if ( request.intervalHours() != null ) {
            entity.setIntervalHours( request.intervalHours() );
        }
        if ( request.intervalDays() != null ) {
            entity.setIntervalDays( request.intervalDays() );
        }
        if ( request.consumable() != null ) {
            entity.setConsumable( request.consumable() );
        }
        if ( request.quantity() != null ) {
            entity.setQuantity( request.quantity() );
        }
        if ( request.quantityUnit() != null ) {
            entity.setQuantityUnit( request.quantityUnit() );
        }
        if ( request.lastPerformedHours() != null ) {
            entity.setLastPerformedHours( request.lastPerformedHours() );
        }
        if ( request.lastPerformedDate() != null ) {
            entity.setLastPerformedDate( request.lastPerformedDate() );
        }
        if ( request.active() != null ) {
            entity.setActive( request.active() );
        }
    }

    @Override
    public WorkOrderResponse toResponse(WorkOrder entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        WorkOrderEntityType entityType = null;
        Long entityId = null;
        WorkOrderType orderType = null;
        WorkOrderPriority priority = null;
        WorkOrderStatus status = null;
        String maintenanceType = null;
        String description = null;
        LocalDate scheduledDate = null;
        Instant startedAt = null;
        Instant completedAt = null;
        BigDecimal mileageAtOrder = null;
        BigDecimal hoursAtOrder = null;
        BigDecimal estimatedCost = null;
        BigDecimal actualCost = null;
        String notes = null;
        Instant createdAt = null;

        id = entity.getId();
        entityType = entity.getEntityType();
        entityId = entity.getEntityId();
        orderType = entity.getOrderType();
        priority = entity.getPriority();
        status = entity.getStatus();
        maintenanceType = entity.getMaintenanceType();
        description = entity.getDescription();
        scheduledDate = entity.getScheduledDate();
        startedAt = entity.getStartedAt();
        completedAt = entity.getCompletedAt();
        mileageAtOrder = entity.getMileageAtOrder();
        hoursAtOrder = entity.getHoursAtOrder();
        estimatedCost = entity.getEstimatedCost();
        actualCost = entity.getActualCost();
        notes = entity.getNotes();
        createdAt = entity.getCreatedAt();

        String entityLabel = null;
        String reference = null;

        WorkOrderResponse workOrderResponse = new WorkOrderResponse( id, reference, entityType, entityId, entityLabel, orderType, priority, status, maintenanceType, description, scheduledDate, startedAt, completedAt, mileageAtOrder, hoursAtOrder, estimatedCost, actualCost, notes, createdAt );

        return workOrderResponse;
    }

    @Override
    public WorkOrder toEntity(WorkOrderRequest request) {
        if ( request == null ) {
            return null;
        }

        WorkOrder.WorkOrderBuilder workOrder = WorkOrder.builder();

        workOrder.entityType( request.entityType() );
        workOrder.entityId( request.entityId() );
        workOrder.orderType( request.orderType() );
        workOrder.priority( request.priority() );
        workOrder.maintenanceType( request.maintenanceType() );
        workOrder.description( request.description() );
        workOrder.scheduledDate( request.scheduledDate() );
        workOrder.mileageAtOrder( request.mileageAtOrder() );
        workOrder.hoursAtOrder( request.hoursAtOrder() );
        workOrder.estimatedCost( request.estimatedCost() );
        workOrder.actualCost( request.actualCost() );
        workOrder.notes( request.notes() );

        workOrder.status( WorkOrderStatus.PLANNED );

        return workOrder.build();
    }

    @Override
    public void updateEntity(WorkOrderRequest request, WorkOrder entity) {
        if ( request == null ) {
            return;
        }

        if ( request.entityType() != null ) {
            entity.setEntityType( request.entityType() );
        }
        if ( request.entityId() != null ) {
            entity.setEntityId( request.entityId() );
        }
        if ( request.orderType() != null ) {
            entity.setOrderType( request.orderType() );
        }
        if ( request.priority() != null ) {
            entity.setPriority( request.priority() );
        }
        if ( request.maintenanceType() != null ) {
            entity.setMaintenanceType( request.maintenanceType() );
        }
        if ( request.description() != null ) {
            entity.setDescription( request.description() );
        }
        if ( request.scheduledDate() != null ) {
            entity.setScheduledDate( request.scheduledDate() );
        }
        if ( request.mileageAtOrder() != null ) {
            entity.setMileageAtOrder( request.mileageAtOrder() );
        }
        if ( request.hoursAtOrder() != null ) {
            entity.setHoursAtOrder( request.hoursAtOrder() );
        }
        if ( request.estimatedCost() != null ) {
            entity.setEstimatedCost( request.estimatedCost() );
        }
        if ( request.actualCost() != null ) {
            entity.setActualCost( request.actualCost() );
        }
        if ( request.notes() != null ) {
            entity.setNotes( request.notes() );
        }
    }

    private Long entityMachineId(MachineMaintenanceRule machineMaintenanceRule) {
        Machine machine = machineMaintenanceRule.getMachine();
        if ( machine == null ) {
            return null;
        }
        return machine.getId();
    }

    private String entityMachineReference(MachineMaintenanceRule machineMaintenanceRule) {
        Machine machine = machineMaintenanceRule.getMachine();
        if ( machine == null ) {
            return null;
        }
        return machine.getReference();
    }
}
