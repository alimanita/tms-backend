package com.transport.tms.controller.fleet;

import com.transport.tms.service.fleet.ChangementHuileService;
import lombok.RequiredArgsConstructor;
import com.transport.tms.controller.api.fleet.ChangementHuileApi;
import com.transport.tms.dto.fleet.request.ChangementHuileRequest;
import com.transport.tms.dto.fleet.response.ChangementHuileResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChangementHuileController implements ChangementHuileApi {

    private final ChangementHuileService changementHuileService;

    @Override
    public ResponseEntity<Page<ChangementHuileResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(changementHuileService.findAll(pageable));
    }

    @Override
    public ResponseEntity<ChangementHuileResponse> findById(Long id) {
        return ResponseEntity.ok(changementHuileService.findById(id));
    }

    @Override
    public ResponseEntity<ChangementHuileResponse> create(ChangementHuileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(changementHuileService.create(request));
    }

    @Override
    public ResponseEntity<ChangementHuileResponse> update(Long id,
                                                          ChangementHuileRequest request) {
        return ResponseEntity.ok(changementHuileService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        changementHuileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ChangementHuileResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(changementHuileService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<ChangementHuileResponse>> findByMachine(Long machineId) {
        return ResponseEntity.ok(changementHuileService.findByMachine(machineId));
    }

    @Override
    public ResponseEntity<List<ChangementHuileResponse>> findAVenir() {
        return ResponseEntity.ok(changementHuileService.findAVenir());
    }
}