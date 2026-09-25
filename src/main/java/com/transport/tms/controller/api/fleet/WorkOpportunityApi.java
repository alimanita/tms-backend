package com.transport.tms.controller.api.fleet;

import com.transport.tms.domain.enums.StatutOffre;
import com.transport.tms.dto.fleet.request.WorkOpportunityImportRequest;
import com.transport.tms.dto.fleet.request.WorkOpportunityStatutUpdateRequest;
import com.transport.tms.dto.fleet.response.WorkOpportunityImportResult;
import com.transport.tms.dto.fleet.response.WorkOpportunityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("api/v1/fleet/work-opportunities")
public interface WorkOpportunityApi {

    @PostMapping("/import")
    ResponseEntity<WorkOpportunityImportResult> importBatch(@RequestBody WorkOpportunityImportRequest request);

    @GetMapping
    ResponseEntity<Page<WorkOpportunityResponse>> getFiltered(
            @RequestParam(required = false) StatutOffre statut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minRevenue,
            @RequestParam(required = false) String departureCity,
            @RequestParam(required = false) String arrivalCity,
            Pageable pageable
    );

    @GetMapping("/nearby")
    ResponseEntity<List<WorkOpportunityResponse>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusKm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime afterDate,
            @RequestParam(required = false) BigDecimal minRevenue,
            @RequestParam(defaultValue = "20") int limit
    );

    @PatchMapping("/{id}/statut")
    ResponseEntity<WorkOpportunityResponse> updateStatut(
            @PathVariable Long id,
            @RequestBody WorkOpportunityStatutUpdateRequest request
    );

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @DeleteMapping("/expired")
    ResponseEntity<Integer> purgeExpired();
}
