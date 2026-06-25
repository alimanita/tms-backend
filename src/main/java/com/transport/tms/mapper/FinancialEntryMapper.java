package com.transport.tms.mapper;

import com.transport.tms.domain.entity.FinancialEntry;
import com.transport.tms.dto.request.FinancialEntryRequest;
import com.transport.tms.dto.response.FinancialEntryResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FinancialEntryMapper {
    FinancialEntryResponse toResponse(FinancialEntry entity);

    @Mapping(target = "id", ignore = true)
    FinancialEntry toEntity(FinancialEntryRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(FinancialEntryRequest request, @MappingTarget FinancialEntry entity);
}
