package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.PlanMaintenance;
import com.transport.tms.dto.fleet.request.PlanMaintenanceRequest;
import com.transport.tms.dto.fleet.response.PlanMaintenanceResponse;
import org.springframework.stereotype.Component;

@Component
public class PlanMaintenanceMapper {

    public PlanMaintenance toEntity(PlanMaintenanceRequest request) {
        PlanMaintenance plan = new PlanMaintenance();
        plan.setEntityType(request.entityType());
        plan.setEntityId(request.entityId());
        plan.setTypeMaintenance(request.typeMaintenance());
        plan.setTriggerType(request.triggerType());
        plan.setTriggerValue(request.triggerValue());
        plan.setTriggerUnit(request.triggerUnit());
        plan.setLastPerformedDate(request.lastPerformedDate());
        plan.setLastPerformedKm(request.lastPerformedKm());
        plan.setLastPerformedHours(request.lastPerformedHours());
        plan.setNextDueDate(request.nextDueDate());
        plan.setNextDueKm(request.nextDueKm());
        plan.setNextDueHours(request.nextDueHours());
        plan.setAlertThreshold(request.alertThreshold());
        plan.setIsActive(true);
        return plan;
    }

    public void updateEntity(PlanMaintenance plan, PlanMaintenanceRequest request) {
        plan.setTypeMaintenance(request.typeMaintenance());
        plan.setTriggerType(request.triggerType());
        plan.setTriggerValue(request.triggerValue());
        plan.setTriggerUnit(request.triggerUnit());
        plan.setNextDueDate(request.nextDueDate());
        plan.setNextDueKm(request.nextDueKm());
        plan.setNextDueHours(request.nextDueHours());
        plan.setAlertThreshold(request.alertThreshold());
    }

    public PlanMaintenanceResponse toResponse(PlanMaintenance plan, String entityRef) {
        return new PlanMaintenanceResponse(
                plan.getId(),
                plan.getEntityType(),
                plan.getEntityId(),
                entityRef,
                plan.getTypeMaintenance(),
                plan.getTypeMaintenance() != null
                        ? plan.getTypeMaintenance().getLabel()
                        : null,
                plan.getTriggerType(),
                plan.getTriggerValue(),
                plan.getTriggerUnit(),
                plan.getLastPerformedDate(),
                plan.getLastPerformedKm(),
                plan.getLastPerformedHours(),
                plan.getNextDueDate(),
                plan.getNextDueKm(),
                plan.getNextDueHours(),
                plan.getAlertThreshold(),
                plan.getIsActive(),
                plan.getCreatedAt()
        );
    }
}