package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.request.SocietePartenaireRequest;
import com.transport.tms.dto.fleet.response.SocietePartenaireResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/fleet/partenaires")
public interface SocietePartenaireApi {

    @PostMapping
    ResponseEntity<SocietePartenaireResponse> create(@RequestBody @Valid SocietePartenaireRequest request);

    @PutMapping("/{id}")
    ResponseEntity<SocietePartenaireResponse> update(@PathVariable Long id, @RequestBody @Valid SocietePartenaireRequest request);

    @GetMapping("/{id}")
    ResponseEntity<SocietePartenaireResponse> findById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<Page<SocietePartenaireResponse>> findAll(Pageable pageable);

    @GetMapping("/actifs")
    ResponseEntity<List<SocietePartenaireResponse>> findAllActive();
}
