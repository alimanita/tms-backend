package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.dto.request.VehicleRequest;
import com.transport.tms.dto.response.VehicleResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {
    VehicleResponse toResponse(Vehicle entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle entity);
}
