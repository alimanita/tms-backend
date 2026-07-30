package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.request.ChangementHuileRequest;
import com.transport.tms.dto.fleet.response.ChangementHuileResponse;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/changements-huile")
public interface ChangementHuileApi {

    @GetMapping
    ResponseEntity<Page<ChangementHuileResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<ChangementHuileResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<ChangementHuileResponse> create(@Valid @RequestBody ChangementHuileRequest request);

    @PutMapping("/{id}")
    ResponseEntity<ChangementHuileResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ChangementHuileRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<ChangementHuileResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/machine/{machineId}")
    ResponseEntity<List<ChangementHuileResponse>> findByMachine(@PathVariable Long machineId);

    @GetMapping("/a-venir")
    ResponseEntity<List<ChangementHuileResponse>> findAVenir();
}