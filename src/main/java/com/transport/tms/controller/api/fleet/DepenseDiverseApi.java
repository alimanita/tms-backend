package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.DepenseDiverseRequest;
import com.transport.tms.dto.fleet.response.DepenseDiverseResponse;
import com.transport.tms.dto.fleet.response.DepenseDiverseSummaryResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/v1/fleet/depenses-diverses")
public interface DepenseDiverseApi {

    @GetMapping
    ResponseEntity<Page<DepenseDiverseResponse>> findAll(
            @RequestParam(value = "chauffeurId", required = false) Long chauffeurId,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Pageable pageable);

    @GetMapping("/summary")
    ResponseEntity<DepenseDiverseSummaryResponse> getSummary(
            @RequestParam(value = "chauffeurId", required = false) Long chauffeurId,
            @RequestParam(value = "categorie", required = false) String categorie,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate);

    @GetMapping("/{id}")
    ResponseEntity<DepenseDiverseResponse> findById(@PathVariable Long id);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DepenseDiverseResponse> create(
            @RequestPart("data") @Valid DepenseDiverseRequest request,
            @RequestPart(value = "proof", required = false) MultipartFile proof);

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DepenseDiverseResponse> update(
            @PathVariable Long id,
            @RequestPart("data") @Valid DepenseDiverseRequest request,
            @RequestPart(value = "proof", required = false) MultipartFile proof);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<DepenseDiverseResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/{id}/proof")
    ResponseEntity<Resource> downloadProof(@PathVariable Long id);

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<com.transport.tms.dto.fleet.response.OcrExpenseResult> extractData(@RequestPart("proof") MultipartFile proof);
}
