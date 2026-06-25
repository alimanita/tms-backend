package com.transport.tms.mapper;

import com.transport.tms.domain.entity.CustomerOrder;
import com.transport.tms.domain.entity.CustomerOrderLine;
import com.transport.tms.dto.response.CustomerOrderResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerOrderMapper {
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    CustomerOrderResponse toResponse(CustomerOrder entity);

    CustomerOrderResponse.LineResponse toLineResponse(CustomerOrderLine line);
}
