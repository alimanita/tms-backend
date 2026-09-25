package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.fleet.WorkOpportunity;
import com.transport.tms.dto.fleet.response.WorkOpportunityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkOpportunityMapper {

    WorkOpportunityResponse toResponse(WorkOpportunity entity);

    default WorkOpportunityResponse toResponse(WorkOpportunity entity, Double emptyKm) {
        WorkOpportunityResponse response = toResponse(entity);
        if (response != null) {
            response.setEmptyKm(emptyKm);
        }
        return response;
    }
}
