package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.DepenseDiverseApi;
import com.transport.tms.dto.fleet.request.DepenseDiverseRequest;
import com.transport.tms.dto.fleet.response.DepenseDiverseResponse;
import com.transport.tms.dto.fleet.response.DepenseDiverseSummaryResponse;
import com.transport.tms.service.fleet.DepenseDiverseService;
import com.transport.tms.service.fleet.ReceiptOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepenseDiverseController implements DepenseDiverseApi {

    private final DepenseDiverseService depenseService;
    private final ReceiptOcrService receiptOcrService;

    @Override
    public ResponseEntity<Page<DepenseDiverseResponse>> findAll(
            Long chauffeurId, String categorie, String startDate, String endDate, Pageable pageable) {
        java.time.LocalDateTime startLdt = parseDate(startDate, false);
        java.time.LocalDateTime endLdt   = parseDate(endDate, true);
        return ResponseEntity.ok(depenseService.findAll(chauffeurId, categorie, startLdt, endLdt, pageable));
    }

    @Override
    public ResponseEntity<DepenseDiverseSummaryResponse> getSummary(
            Long chauffeurId, String categorie, String startDate, String endDate) {
        java.time.LocalDateTime startLdt = parseDate(startDate, false);
        java.time.LocalDateTime endLdt   = parseDate(endDate, true);
        return ResponseEntity.ok(depenseService.getSummary(chauffeurId, categorie, startLdt, endLdt));
    }

    @Override
    public ResponseEntity<DepenseDiverseResponse> findById(Long id) {
        return ResponseEntity.ok(depenseService.findById(id));
    }

    @Override
    public ResponseEntity<DepenseDiverseResponse> create(DepenseDiverseRequest request, MultipartFile proof) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depenseService.create(request, proof));
    }

    @Override
    public ResponseEntity<DepenseDiverseResponse> update(Long id, DepenseDiverseRequest request, MultipartFile proof) {
        return ResponseEntity.ok(depenseService.update(id, request, proof));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(Long id) {
        depenseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<DepenseDiverseResponse>> findByChauffeur(Long chauffeurId) {
        return ResponseEntity.ok(depenseService.findByChauffeur(chauffeurId));
    }

    @Override
    public ResponseEntity<Resource> downloadProof(Long id) {
        Resource file = depenseService.getProofFile(id);
        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(file.getURI()));
        } catch (IOException e) {
            contentType = null;
        }
        if (contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @Override
    public ResponseEntity<com.transport.tms.dto.fleet.response.OcrExpenseResult> extractData(MultipartFile proof) {
        return ResponseEntity.ok(receiptOcrService.extractExpenseData(proof));
    }

    // ── Helper ───────────────────────────────────────────────────────────

    private java.time.LocalDateTime parseDate(String dateStr, boolean endOfDay) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDate d = LocalDate.parse(dateStr);
            return endOfDay ? d.atTime(23, 59, 59) : d.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }
}
