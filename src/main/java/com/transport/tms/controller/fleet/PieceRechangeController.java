package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.PieceRechangeApi;
import com.transport.tms.dto.fleet.request.PieceRechangeRequest;
import com.transport.tms.dto.fleet.response.PieceRechangeResponse;
import com.transport.tms.service.fleet.PieceRechangeService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PieceRechangeController implements PieceRechangeApi {

    private final PieceRechangeService pieceRechangeService;

    @Override
    public ResponseEntity<Page<PieceRechangeResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(pieceRechangeService.findAll(pageable));
    }

    @Override
    public ResponseEntity<PieceRechangeResponse> findById(Long id) {
        return ResponseEntity.ok(pieceRechangeService.findById(id));
    }

    @Override
    public ResponseEntity<PieceRechangeResponse> create(PieceRechangeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pieceRechangeService.create(request));
    }

    @Override
    public ResponseEntity<PieceRechangeResponse> update(Long id, PieceRechangeRequest request) {
        return ResponseEntity.ok(pieceRechangeService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        pieceRechangeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PieceRechangeResponse>> findStockFaible() {
        return ResponseEntity.ok(pieceRechangeService.findStockFaible());
    }

    @Override
    public ResponseEntity<PieceRechangeResponse> updateStock(Long id, BigDecimal quantite) {
        return ResponseEntity.ok(pieceRechangeService.updateStock(id, quantite));
    }

    @Override
    public ResponseEntity<PieceRechangeResponse> uploadProof(Long id, org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(pieceRechangeService.uploadProofFile(id, file));
    }

    @Override
    public ResponseEntity<org.springframework.core.io.Resource> getProofFile(Long id) {
        org.springframework.core.io.Resource resource = pieceRechangeService.getProofFile(id);
        
        String contentType = "application/octet-stream";
        String filename = resource.getFilename();
        if (filename != null) {
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }
        }
        
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }

    @Override
    public ResponseEntity<com.transport.tms.dto.fleet.response.OcrPieceResult> extractPieceData(org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(pieceRechangeService.extractPieceData(file));
    }
}