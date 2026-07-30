package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.request.ChauffeurRequest;
import jakarta.validation.Valid;

import com.transport.tms.dto.fleet.response.ChauffeurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/chauffeurs")
public interface ChauffeurApi {
    @GetMapping
    ResponseEntity<Page<ChauffeurResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<ChauffeurResponse> findById(@PathVariable Long id);
    @GetMapping("/me")
    ResponseEntity<ChauffeurResponse> findMe();
    @PostMapping
    ResponseEntity<ChauffeurResponse> create(@Valid @RequestBody ChauffeurRequest request);

    @PutMapping("/{id}")
    ResponseEntity<ChauffeurResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody ChauffeurRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/actifs")
    ResponseEntity<List<ChauffeurResponse>> findActifs();
}
