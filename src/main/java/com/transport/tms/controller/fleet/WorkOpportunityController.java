package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.WorkOpportunityApi;
import com.transport.tms.domain.enums.StatutOffre;
import com.transport.tms.dto.fleet.request.WorkOpportunityImportRequest;
import com.transport.tms.dto.fleet.request.WorkOpportunityStatutUpdateRequest;
import com.transport.tms.dto.fleet.response.WorkOpportunityImportResult;
import com.transport.tms.dto.fleet.response.WorkOpportunityResponse;
import com.transport.tms.service.fleet.WorkOpportunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/fleet/work-opportunities")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class WorkOpportunityController implements WorkOpportunityApi {

    private final WorkOpportunityService service;

    @Override
    public ResponseEntity<WorkOpportunityImportResult> importBatch(WorkOpportunityImportRequest request) {
        return ResponseEntity.ok(service.importBatch(request));
    }

    @Override
    public ResponseEntity<Page<WorkOpportunityResponse>> getFiltered(
            StatutOffre statut, LocalDateTime startDate, LocalDateTime endDate,
            BigDecimal minRevenue, String departureCity, String arrivalCity,
            Pageable pageable) {
        return ResponseEntity.ok(service.getFiltered(statut, startDate, endDate, minRevenue, departureCity, arrivalCity, pageable));
    }

    @Override
    public ResponseEntity<List<WorkOpportunityResponse>> findNearby(
            double lat, double lng, double radiusKm, LocalDateTime afterDate,
            BigDecimal minRevenue, int limit) {
        return ResponseEntity.ok(service.findNearby(lat, lng, radiusKm, afterDate, minRevenue, limit));
    }

    @Override
    public ResponseEntity<WorkOpportunityResponse> updateStatut(Long id, WorkOpportunityStatutUpdateRequest request) {
        return ResponseEntity.ok(service.updateStatut(id, request.getStatut()));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Integer> purgeExpired() {
        int deletedCount = service.purgeExpired();
        return ResponseEntity.ok(deletedCount);
    }
}
