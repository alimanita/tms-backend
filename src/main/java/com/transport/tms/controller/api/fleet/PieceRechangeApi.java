package com.transport.tms.controller.api.fleet;

import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.PieceRechangeRequest;
import com.transport.tms.dto.fleet.response.PieceRechangeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/pieces-rechange")
public interface PieceRechangeApi {

    @GetMapping
    ResponseEntity<Page<PieceRechangeResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<PieceRechangeResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<PieceRechangeResponse> create(@Valid @RequestBody PieceRechangeRequest request);

    @PutMapping("/{id}")
    ResponseEntity<PieceRechangeResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody PieceRechangeRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/stock-faible")
    ResponseEntity<List<PieceRechangeResponse>> findStockFaible();

    @PatchMapping("/{id}/stock")
    ResponseEntity<PieceRechangeResponse> updateStock(@PathVariable Long id,
                                                      @RequestParam java.math.BigDecimal quantite);

    @PostMapping(value = "/{id}/proof", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<PieceRechangeResponse> uploadProof(@PathVariable Long id,
                                                      @RequestParam("file") org.springframework.web.multipart.MultipartFile file);

    @GetMapping("/{id}/proof")
    ResponseEntity<org.springframework.core.io.Resource> getProofFile(@PathVariable Long id);

    @PostMapping(value = "/extract", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<com.transport.tms.dto.fleet.response.OcrPieceResult> extractPieceData(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file);
}