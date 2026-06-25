package com.transport.tms.mapper;

import com.transport.tms.domain.entity.SparePart;
import com.transport.tms.dto.request.SparePartRequest;
import com.transport.tms.dto.response.SparePartResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SparePartMapper {
    SparePartResponse toResponse(SparePart entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    SparePart toEntity(SparePartRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(SparePartRequest request, @MappingTarget SparePart entity);
}
