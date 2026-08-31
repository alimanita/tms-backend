package com.transport.tms.controller.fleet;

import com.transport.tms.dto.fleet.request.ChauffeurConfigRequest;
import com.transport.tms.service.fleet.ChauffeurService;
import lombok.RequiredArgsConstructor;
import com.transport.tms.controller.api.fleet.ChauffeurApi;
import com.transport.tms.dto.fleet.request.ChauffeurRequest;
import com.transport.tms.dto.fleet.response.ChauffeurResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChauffeurController implements ChauffeurApi {

    private final ChauffeurService chauffeurService;

    @Override
    public ResponseEntity<Page<ChauffeurResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(chauffeurService.getAll(pageable));
    }

    @Override
    public ResponseEntity<ChauffeurResponse> findById(Long id) {
        return ResponseEntity.ok(chauffeurService.getById(id));
    }

    @Override
    public ResponseEntity<ChauffeurResponse> findMe() {
        return ResponseEntity.ok(chauffeurService.findMe());
    }

    @Override
    public ResponseEntity<ChauffeurResponse> create(ChauffeurRequest request) {
        return ResponseEntity.ok(chauffeurService.create(request));
    }

    @Override
    public ResponseEntity<ChauffeurResponse> update(Long id, ChauffeurRequest request) {
        return ResponseEntity.ok(chauffeurService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        chauffeurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ChauffeurResponse>> findActifs() {
        return ResponseEntity.ok(chauffeurService.getAllActive());
    }

    @Override
    public ResponseEntity<ChauffeurResponse> toggleActif(Long id) {
        return ResponseEntity.ok(chauffeurService.toggleActif(id));
    }

    @Override
    public ResponseEntity<List<ChauffeurResponse>> updateSettings(ChauffeurConfigRequest request) {
        return ResponseEntity.ok(chauffeurService.updateSettings(request));
    }
}
