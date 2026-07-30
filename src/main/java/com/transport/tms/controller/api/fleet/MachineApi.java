package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.MachineRequest;
import com.transport.tms.dto.fleet.request.UpdateHeuresRequest;
import com.transport.tms.dto.fleet.response.MachineResponse;
import com.transport.tms.dto.fleet.response.UpdateHeuresResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/machines")
public interface MachineApi {
    @GetMapping
    ResponseEntity<Page<MachineResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<MachineResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<MachineResponse> create(@Valid @RequestBody MachineRequest request);

    @PutMapping("/{id}")
    ResponseEntity<MachineResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody MachineRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/actives")
    ResponseEntity<List<MachineResponse>> findActives();
    @PatchMapping("/{id}/heures")
    ResponseEntity<UpdateHeuresResponse> updateHeuresActuelles(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateHeuresRequest request);

}
