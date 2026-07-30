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
}