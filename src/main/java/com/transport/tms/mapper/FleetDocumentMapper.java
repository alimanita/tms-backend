package com.transport.tms.mapper;

import com.transport.tms.domain.entity.FleetDocument;
import com.transport.tms.dto.request.FleetDocumentRequest;
import com.transport.tms.dto.response.FleetDocumentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FleetDocumentMapper {
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.registration", target = "vehicleRegistration")
    @Mapping(source = "driver.id", target = "driverId")
    @Mapping(source = "driver.fullName", target = "driverName")
    FleetDocumentResponse toResponse(FleetDocument entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FleetDocument toEntity(FleetDocumentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "driver", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(FleetDocumentRequest request, @MappingTarget FleetDocument entity);
}
