package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.PeageApi;
import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.response.PeageResponse;
import com.transport.tms.dto.fleet.response.OcrTollResult;
import com.transport.tms.service.fleet.PeageService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PeageController implements PeageApi {

    private final PeageService peageService;

    @Override
    public ResponseEntity<Page<PeageResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(peageService.findAll(pageable));
    }

    @Override
    public ResponseEntity<PeageResponse> findById(Long id) {
        return ResponseEntity.ok(peageService.findById(id));
    }

    @Override
    public ResponseEntity<PeageResponse> create(PeageRequest request, MultipartFile proof) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(peageService.create(request, proof));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        peageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PeageResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(peageService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<PeageResponse>> findByChauffeur(Long chauffeurId) {
        return ResponseEntity.ok(peageService.findByChauffeur(chauffeurId));
    }

    @Override
    public ResponseEntity<List<PeageResponse>> findByMission(Long missionId) {
        return ResponseEntity.ok(peageService.findByMission(missionId));
    }

    @Override
    public ResponseEntity<Resource> downloadProof(Long id) {
        Resource file = peageService.getProofFile(id);

        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(file.getURI()));
        } catch (IOException e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @Override
    public ResponseEntity<OcrTollResult> extractTollData(MultipartFile proof) {
        return ResponseEntity.ok(peageService.extractTollData(proof));
    }
}
