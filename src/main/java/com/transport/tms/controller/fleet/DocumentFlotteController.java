package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.DocumentFlotteApi;
import com.transport.tms.dto.fleet.request.DocumentFlotteRequest;
import com.transport.tms.dto.fleet.response.DocumentFlotteResponse;
import com.transport.tms.service.fleet.DocumentFlotteService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentFlotteController implements DocumentFlotteApi {

    private final DocumentFlotteService documentFlotteService;
    private final com.transport.tms.service.fleet.ReceiptOcrService receiptOcrService;

    @Override
    public ResponseEntity<Page<DocumentFlotteResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(documentFlotteService.findAll(pageable));
    }

    @Override
    public ResponseEntity<DocumentFlotteResponse> findById(Long id) {
        return ResponseEntity.ok(documentFlotteService.findById(id));
    }

    @Override
    public ResponseEntity<DocumentFlotteResponse> create(
            @org.springframework.web.bind.annotation.RequestPart("data") DocumentFlotteRequest request,
            @org.springframework.web.bind.annotation.RequestPart(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentFlotteService.create(request, file));
    }

    @Override
    public ResponseEntity<DocumentFlotteResponse> update(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestPart("data") DocumentFlotteRequest request,
            @org.springframework.web.bind.annotation.RequestPart(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(documentFlotteService.update(id, request, file));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        documentFlotteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<DocumentFlotteResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(documentFlotteService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<DocumentFlotteResponse>> findByChauffeur(Long chauffeurId) {
        return ResponseEntity.ok(documentFlotteService.findByChauffeur(chauffeurId));
    }

    @Override
    public ResponseEntity<List<DocumentFlotteResponse>> findByMachine(Long machineId) {
        return ResponseEntity.ok(documentFlotteService.findByMachine(machineId));
    }

    @Override
    public ResponseEntity<List<DocumentFlotteResponse>> findExpirantBientot(int jours) {
        return ResponseEntity.ok(documentFlotteService.findExpirantBientot(jours));
    }

    @Override
    public ResponseEntity<List<DocumentFlotteResponse>> findExpires() {
        return ResponseEntity.ok(documentFlotteService.findExpires());
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@org.springframework.web.bind.annotation.PathVariable Long id) {
        org.springframework.core.io.Resource file = documentFlotteService.getFile(id);

        String contentType;
        try {
            contentType = java.nio.file.Files.probeContentType(java.nio.file.Paths.get(file.getURI()));
        } catch (java.io.IOException e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @Override
    public ResponseEntity<com.transport.tms.dto.fleet.response.OcrDocumentResult> extractAi(
            org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(receiptOcrService.extractDocumentData(file));
    }
}