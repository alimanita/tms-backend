package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Tire;
import com.transport.tms.dto.request.TireRequest;
import com.transport.tms.dto.response.TireResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TireMapper {
    TireResponse toResponse(Tire entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Tire toEntity(TireRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(TireRequest request, @MappingTarget Tire entity);
}
