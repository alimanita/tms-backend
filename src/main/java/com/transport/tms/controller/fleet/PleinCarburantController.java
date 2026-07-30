package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.PleinCarburantApi;
import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import com.transport.tms.service.fleet.PleinCarburantService;
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
public class PleinCarburantController implements PleinCarburantApi {

    private final PleinCarburantService pleinCarburantService;

    @Override
    public ResponseEntity<Page<PleinCarburantResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(pleinCarburantService.findAll(pageable));
    }

    @Override
    public ResponseEntity<PleinCarburantResponse> findById(Long id) {
        return ResponseEntity.ok(pleinCarburantService.findById(id));
    }



    @Override
    public ResponseEntity<PleinCarburantResponse> create(PleinCarburantRequest request, MultipartFile proof) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pleinCarburantService.create(request, proof));
    }


    @Override
    public ResponseEntity<Void> delete(Long id) {
        pleinCarburantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PleinCarburantResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(pleinCarburantService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<PleinCarburantResponse>> findByChauffeur(Long chauffeurId) {
        return ResponseEntity.ok(pleinCarburantService.findByChauffeur(chauffeurId));
    }

    @Override
    public ResponseEntity<Resource> downloadProof(Long id) {
        Resource file = pleinCarburantService.getProofFile(id);

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
}