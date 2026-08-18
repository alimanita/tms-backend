package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.MissionApi;
import com.transport.tms.dto.fleet.request.DepenseMissionRequest;
import com.transport.tms.dto.fleet.request.MissionClotureRequest;
import com.transport.tms.dto.fleet.request.MissionRequest;
import com.transport.tms.dto.fleet.response.DepenseMissionResponse;
import com.transport.tms.dto.fleet.response.MissionResponse;
import com.transport.tms.service.fleet.MissionService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MissionController implements MissionApi {

    private final MissionService missionService;

    @Override
    public ResponseEntity<Page<MissionResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(missionService.findAll(pageable));
    }

    @Override
    public ResponseEntity<MissionResponse> findById(Long id) {
        return ResponseEntity.ok(missionService.findById(id));
    }

    @Override
    public ResponseEntity<MissionResponse> create(MissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(missionService.create(request));
    }

    @Override
    public ResponseEntity<MissionResponse> update(Long id, MissionRequest request) {
        return ResponseEntity.ok(missionService.update(id, request));
    }

/*    @Override
    public ResponseEntity<MissionResponse> soumettre(Long id) {
        return ResponseEntity.ok(missionService.soumettre(id));
    }

    @Override
    public ResponseEntity<MissionResponse> approuver(Long id) {
        return ResponseEntity.ok(missionService.approuver(id));
    }

    @Override
    public ResponseEntity<MissionResponse> rejeter(Long id, String motif) {
        return ResponseEntity.ok(missionService.rejeter(id, motif));
    }*/

    @Override
    public ResponseEntity<MissionResponse> demarrer(Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.math.BigDecimal mileageAtDeparture) {
        return ResponseEntity.ok(missionService.demarrer(id, mileageAtDeparture));
    }

    @Override
    public ResponseEntity<MissionResponse> cloturer(Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.math.BigDecimal mileageAtReturn) {
        return ResponseEntity.ok(missionService.cloturer(id, mileageAtReturn));
    }

    @Override
    public ResponseEntity<MissionResponse> annuler(Long id, String motif) {
        return ResponseEntity.ok(missionService.annuler(id, motif));
    }

    @Override
    public ResponseEntity<DepenseMissionResponse> addDepense(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestPart("depense") DepenseMissionRequest request,
            @org.springframework.web.bind.annotation.RequestPart(value = "receipt", required = false) org.springframework.web.multipart.MultipartFile receipt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(missionService.addDepense(id, request, receipt));
    }

    @Override
    public ResponseEntity<List<DepenseMissionResponse>> findDepenses(Long id) {
        return ResponseEntity.ok(missionService.findDepenses(id));
    }

    @Override
    public ResponseEntity<Void> removeDepense(Long id, Long depenseId) {
        missionService.removeDepense(id, depenseId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadDepenseReceipt(Long id, Long depenseId) {
        org.springframework.core.io.Resource file = missionService.getDepenseReceipt(id, depenseId);
        String contentType = "application/octet-stream";
        try {
            contentType = java.nio.file.Files.probeContentType(java.nio.file.Paths.get(file.getURI()));
        } catch (java.io.IOException ex) {
            // Ignorer
        }
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @Override
    public ResponseEntity<List<MissionResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(missionService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<MissionResponse>> findByChauffeur(Long chauffeurId) {
        return ResponseEntity.ok(missionService.findByChauffeur(chauffeurId));
    }

    @Override
    public ResponseEntity<List<MissionResponse>> findEnCours() {
        return ResponseEntity.ok(missionService.findEnCours());
    }
    @Override
    public ResponseEntity<List<MissionResponse>> findMesMissions() {
        return ResponseEntity.ok(missionService.findMesMissions());
    }

    @Override
    public ResponseEntity<Page<DepenseMissionResponse>> findAllTolls(Pageable pageable) {
        return ResponseEntity.ok(missionService.findAllTolls(pageable));
    }
}