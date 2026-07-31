package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/fleet/vehicules")
public interface VehiculeApi {
    @GetMapping
    ResponseEntity<Page<VehiculeResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<VehiculeResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<VehiculeResponse> create(@Valid @RequestBody VehiculeRequest request);

    @PutMapping("/{id}")
    ResponseEntity<VehiculeResponse> update(@PathVariable Long id,
                                            @Valid @RequestBody VehiculeRequest request);

    @PatchMapping("/{id}/statut")
    ResponseEntity<VehiculeResponse> updateStatut(@PathVariable Long id,
                                                  @RequestParam String statut);
    @GetMapping("/disponibles")
    ResponseEntity<List<VehiculeResponse>> findDisponibles();
    @PatchMapping("/{id}/toggle-actif")
    ResponseEntity<VehiculeResponse> toggleActif(@PathVariable Long id);
}
