package com.transport.tms.controller.api.fleet;

import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import com.transport.tms.dto.fleet.response.PneuResponse;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/pneus")
public interface AffectationPneuApi {

    // ── Gestion des pneus (stock) ─────────────────────────────

    @GetMapping
    ResponseEntity<Page<PneuResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PneuResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<PneuResponse> create(@Valid @RequestBody PneuRequest request);

    @PutMapping("/{id}")
    ResponseEntity<PneuResponse> update(@PathVariable Long id,
                                        @Valid @RequestBody PneuRequest request);

    @GetMapping("/stock")
    ResponseEntity<List<PneuResponse>> findEnStock();

    // ── Affectations ──────────────────────────────────────────

    @GetMapping("/affectations")
    ResponseEntity<Page<AffectationPneuResponse>> findAllAffectations(Pageable pageable);

    @PostMapping("/affectations")
    ResponseEntity<AffectationPneuResponse> affecter(@Valid @RequestBody AffectationPneuRequest request);

    @PatchMapping("/affectations/{id}/demonter")
    ResponseEntity<AffectationPneuResponse> demonter(@PathVariable Long id,
                                                     @RequestParam java.math.BigDecimal unmountMileage,
                                                     @RequestParam AffectationPneu.RaisonDemontage raison);

    @GetMapping("/affectations/vehicule/{vehiculeId}")
    ResponseEntity<List<AffectationPneuResponse>> findByVehicule(@PathVariable Long vehiculeId);
}