package com.transport.tms.service;

import com.transport.tms.domain.entity.PieceRechange;
import com.transport.tms.dto.request.PieceRechangeRequest;
import com.transport.tms.dto.response.PieceRechangeResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.PieceRechangeMapper;
import com.transport.tms.repository.PieceRechangeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PieceRechangeService   {

    private final PieceRechangeRepository pieceRepository;
    private final PieceRechangeMapper mapper;


    public PieceRechangeResponse create(PieceRechangeRequest request) {
        if (pieceRepository.existsByReference(request.reference())) {
            throw new InvalidOperationException(
                    "Une pièce avec la référence '" + request.reference() + "' existe déjà");
        }
        return mapper.toResponse(pieceRepository.save(mapper.toEntity(request)));
    }


    public PieceRechangeResponse update(Long id, PieceRechangeRequest request) {
        PieceRechange piece = findEntityById(id);
        mapper.updateEntity(piece, request);
        return mapper.toResponse(pieceRepository.save(piece));
    }


    public PieceRechangeResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }


    @Transactional(readOnly = true)
    public Page<PieceRechangeResponse> findAll(Pageable pageable) {
        return pieceRepository.findByIsActiveTrue(pageable).map(mapper::toResponse);
    }



    public List<PieceRechangeResponse> findStockFaible() {
        return pieceRepository.findStockFaible()
                .stream().map(mapper::toResponse).toList();
    }


    public PieceRechangeResponse updateStock(Long id, BigDecimal quantite) {
        PieceRechange piece = findEntityById(id);
        BigDecimal newQty = piece.getStockQty().add(quantite);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOperationException(
                    "Stock insuffisant. Stock actuel : " + piece.getStockQty());
        }
        piece.setStockQty(newQty);
        log.info("Stock pièce {} mis à jour : {} → {}", piece.getReference(),
                piece.getStockQty(), newQty);
        return mapper.toResponse(pieceRepository.save(piece));
    }


    public void delete(Long id) {
        PieceRechange piece = findEntityById(id);
        piece.setIsActive(false);
        pieceRepository.save(piece);
    }

    private PieceRechange findEntityById(Long id) {
        return pieceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pièce de rechange introuvable avec l'ID = " + id));
    }
}