package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.VehiculeApi;
import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import com.transport.tms.service.fleet.VehiculeService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehiculeController implements VehiculeApi {

    private final VehiculeService vehiculeService;

    @Override
    public ResponseEntity<Page<VehiculeResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(vehiculeService.findAll(pageable));
    }

    @Override
    public ResponseEntity<VehiculeResponse> findById(Long id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    @Override
    public ResponseEntity<VehiculeResponse> create(VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehiculeService.create(request));
    }

    @Override
    public ResponseEntity<VehiculeResponse> update(Long id, VehiculeRequest request) {
        return ResponseEntity.ok(vehiculeService.update(id, request));
    }

    @Override
    public ResponseEntity<VehiculeResponse> updateStatut(Long id, String statut) {
        return ResponseEntity.ok(vehiculeService.updateStatut(id, statut));
    }

    @Override
    public ResponseEntity<List<VehiculeResponse>> findDisponibles() {
        return ResponseEntity.ok(vehiculeService.findDisponibles());
    }
}