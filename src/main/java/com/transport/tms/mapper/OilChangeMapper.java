package com.transport.tms.mapper;

import com.transport.tms.domain.entity.OilChange;
import com.transport.tms.dto.request.OilChangeRequest;
import com.transport.tms.dto.response.OilChangeResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OilChangeMapper {
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.registration", target = "vehicleRegistration")
    OilChangeResponse toResponse(OilChange entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OilChange toEntity(OilChangeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(OilChangeRequest request, @MappingTarget OilChange entity);
}
