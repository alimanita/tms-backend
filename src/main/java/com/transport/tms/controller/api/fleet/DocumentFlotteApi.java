package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.DocumentFlotteRequest;
import com.transport.tms.dto.fleet.response.DocumentFlotteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/documents")
public interface DocumentFlotteApi {

    @GetMapping
    ResponseEntity<Page<DocumentFlotteResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<DocumentFlotteResponse> findById(@PathVariable Long id);

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DocumentFlotteResponse> create(@RequestPart("data") @Valid DocumentFlotteRequest request,
                                                  @RequestPart(value = "file", required = false) org.springframework.web.multipart.MultipartFile file);

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DocumentFlotteResponse> update(@PathVariable Long id,
                                                  @RequestPart("data") @Valid DocumentFlotteRequest request,
                                                  @RequestPart(value = "file", required = false) org.springframework.web.multipart.MultipartFile file);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<DocumentFlotteResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<DocumentFlotteResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/machine/{machineId}")
    ResponseEntity<List<DocumentFlotteResponse>> findByMachine(@PathVariable Long machineId);

    @GetMapping("/expirant")
    ResponseEntity<List<DocumentFlotteResponse>> findExpirantBientot(@RequestParam(defaultValue = "30") int jours);

    @GetMapping("/expires")
    ResponseEntity<List<DocumentFlotteResponse>> findExpires();

    @GetMapping("/{id}/file")
    ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable Long id);
}