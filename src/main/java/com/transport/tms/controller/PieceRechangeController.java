package com.transport.tms.controller;

import com.transport.tms.dto.request.PieceRechangeRequest;
import com.transport.tms.dto.response.PieceRechangeResponse;
import com.transport.tms.service.PieceRechangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet/pieces-rechange")
@RequiredArgsConstructor
public class PieceRechangeController {

    private final PieceRechangeService pieceRechangeService;

    @GetMapping
    public ResponseEntity<Page<PieceRechangeResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(pieceRechangeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceRechangeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pieceRechangeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PieceRechangeResponse> create(@RequestBody PieceRechangeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pieceRechangeService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PieceRechangeResponse> update(@PathVariable Long id, @RequestBody PieceRechangeRequest request) {
        return ResponseEntity.ok(pieceRechangeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pieceRechangeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock-faible")
    public ResponseEntity<List<PieceRechangeResponse>> findStockFaible() {
        return ResponseEntity.ok(pieceRechangeService.findStockFaible());
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<PieceRechangeResponse> updateStock(@PathVariable Long id, @RequestParam BigDecimal quantite) {
        return ResponseEntity.ok(pieceRechangeService.updateStock(id, quantite));
    }
}