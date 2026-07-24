package com.transport.tms.mapper;

import com.transport.tms.domain.entity.TireAssignment;
import com.transport.tms.dto.request.TireAssignmentRequest;
import com.transport.tms.dto.response.TireAssignmentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TireAssignmentMapper {
    @Mapping(source = "tire.id", target = "tireId")
    @Mapping(source = "tire.serialNumber", target = "tireSerialNumber")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.registration", target = "vehicleRegistration")
    TireAssignmentResponse toResponse(TireAssignment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tire", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TireAssignment toEntity(TireAssignmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tire", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(TireAssignmentRequest request, @MappingTarget TireAssignment entity);
}
