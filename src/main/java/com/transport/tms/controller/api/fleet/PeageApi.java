package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.response.PeageResponse;
import com.transport.tms.dto.fleet.response.OcrTollResult;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("api/v1/fleet/peages")
public interface PeageApi {

    @GetMapping
    ResponseEntity<Page<PeageResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PeageResponse> findById(@PathVariable Long id);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PeageResponse> create(
            @RequestPart("data") @Valid PeageRequest request,
            @RequestPart(value = "proof", required = false) MultipartFile proof);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<PeageResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<PeageResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/mission/{missionId}")
    ResponseEntity<List<PeageResponse>> findByMission(@PathVariable Long missionId);

    @GetMapping("/{id}/proof")
    ResponseEntity<Resource> downloadProof(@PathVariable Long id);

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<OcrTollResult> extractTollData(@RequestPart("proof") MultipartFile proof);
}
