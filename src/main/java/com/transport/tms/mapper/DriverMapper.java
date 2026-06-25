package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.dto.request.DriverRequest;
import com.transport.tms.dto.response.DriverResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DriverMapper {
    @Mapping(target = "fullName", expression = "java(entity.getFullName())")
    DriverResponse toResponse(Driver entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Driver toEntity(DriverRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(DriverRequest request, @MappingTarget Driver entity);
}
