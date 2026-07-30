package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RequestMapping("api/v1/fleet/pleins-carburant")
public interface PleinCarburantApi {

    @GetMapping
    ResponseEntity<Page<PleinCarburantResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PleinCarburantResponse> findById(@PathVariable Long id);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PleinCarburantResponse> create(
            @RequestPart("data") @Valid PleinCarburantRequest request,
            @RequestPart(value = "proof", required = false) MultipartFile proof);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<PleinCarburantResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<PleinCarburantResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/{id}/proof")
    ResponseEntity<Resource> downloadProof(@PathVariable Long id);
}