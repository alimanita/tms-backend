package com.transport.tms.mapper;

import com.transport.tms.domain.entity.MaintenanceRecord;
import com.transport.tms.dto.response.MaintenanceRecordResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaintenanceRecordMapper {
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleRegistration", source = "vehicle.registration")
    MaintenanceRecordResponse toResponse(MaintenanceRecord entity);
}
