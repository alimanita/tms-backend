package com.transport.tms.mapper;

import com.transport.tms.domain.entity.MaintenancePlan;
import com.transport.tms.dto.request.MaintenancePlanRequest;
import com.transport.tms.dto.response.MaintenancePlanResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaintenancePlanMapper {
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.registration", target = "vehicleRegistration")
    MaintenancePlanResponse toResponse(MaintenancePlan entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    MaintenancePlan toEntity(MaintenancePlanRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MaintenancePlanRequest request, @MappingTarget MaintenancePlan entity);
}
