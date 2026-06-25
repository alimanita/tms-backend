package com.transport.tms.mapper;

import com.transport.tms.domain.entity.FuelRecord;
import com.transport.tms.dto.response.FuelRecordResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FuelRecordMapper {
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleRegistration", source = "vehicle.registration")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", expression = "java(entity.getDriver() != null ? entity.getDriver().getFullName() : null)")
    FuelRecordResponse toResponse(FuelRecord entity);
}
