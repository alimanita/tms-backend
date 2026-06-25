package com.transport.tms.mapper;

import com.transport.tms.domain.entity.TransportMission;
import com.transport.tms.dto.response.TransportMissionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransportMissionMapper {
    @Mapping(target = "customerOrderId", source = "customerOrder.id")
    @Mapping(target = "customerOrderReference", source = "customerOrder.reference")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleRegistration", source = "vehicle.registration")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", expression = "java(entity.getDriver() != null ? entity.getDriver().getFullName() : null)")
    TransportMissionResponse toResponse(TransportMission entity);
}
