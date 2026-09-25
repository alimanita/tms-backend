package com.transport.tms.service.fleet;

import com.transport.tms.domain.enums.StatutOffre;
import com.transport.tms.dto.fleet.request.WorkOpportunityImportRequest;
import com.transport.tms.dto.fleet.response.WorkOpportunityImportResult;
import com.transport.tms.dto.fleet.response.WorkOpportunityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WorkOpportunityService {

    WorkOpportunityImportResult importBatch(WorkOpportunityImportRequest request);

    Page<WorkOpportunityResponse> getFiltered(StatutOffre statut, LocalDateTime startDate, LocalDateTime endDate,
                                              BigDecimal minRevenue, String departureCity, String arrivalCity,
                                              Pageable pageable);

    List<WorkOpportunityResponse> findNearby(double lat, double lng, double radiusKm, LocalDateTime afterDate,
                                             BigDecimal minRevenue, int limit);

    WorkOpportunityResponse updateStatut(Long id, StatutOffre statut);

    void delete(Long id);

    int purgeExpired();
}
