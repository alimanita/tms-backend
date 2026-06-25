package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Customer;
import com.transport.tms.dto.request.CustomerRequest;
import com.transport.tms.dto.response.CustomerResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerResponse toResponse(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CustomerRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CustomerRequest request, @MappingTarget Customer entity);
}
