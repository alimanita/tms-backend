package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.PieceRechange;
import com.transport.tms.dto.fleet.request.PieceRechangeRequest;
import com.transport.tms.dto.fleet.response.PieceRechangeResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.PieceRechangeMapper;
import com.transport.tms.repository.fleet.PieceRechangeRepository;
import com.transport.tms.service.fleet.PieceRechangeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PieceRechangeServiceImpl implements PieceRechangeService {

    private final PieceRechangeRepository pieceRepository;
    private final PieceRechangeMapper mapper;
    private final com.transport.tms.service.fleet.FileStorageService fileStorageService;

    @Override
    public PieceRechangeResponse create(PieceRechangeRequest request) {
        if (pieceRepository.existsByReference(request.reference())) {
            throw new InvalidOperationException(
                    "Une pièce avec la référence '" + request.reference() + "' existe déjà");
        }
        return mapper.toResponse(pieceRepository.save(mapper.toEntity(request)));
    }

    @Override
    public PieceRechangeResponse update(Long id, PieceRechangeRequest request) {
        PieceRechange piece = findEntityById(id);
        mapper.updateEntity(piece, request);
        return mapper.toResponse(pieceRepository.save(piece));
    }

    @Override
    @Transactional(readOnly = true)
    public PieceRechangeResponse findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PieceRechangeResponse> findAll(Pageable pageable) {
        return pieceRepository.findByIsActiveTrue(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PieceRechangeResponse> findStockFaible() {
        return pieceRepository.findStockFaible()
                .stream().map(mapper::toResponse).toList();
    }

    @Override
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

    @Override
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

    @Override
    public PieceRechangeResponse uploadProofFile(Long id, org.springframework.web.multipart.MultipartFile file) {
        PieceRechange piece = findEntityById(id);
        if (file != null && !file.isEmpty()) {
            piece.setReceiptPath(fileStorageService.store(file));
            pieceRepository.save(piece);
        }
        return mapper.toResponse(piece);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.core.io.Resource getProofFile(Long id) {
        PieceRechange piece = findEntityById(id);
        if (piece.getReceiptPath() == null) {
            throw new EntityNotFoundException("Aucun justificatif pour cette pièce");
        }
        return fileStorageService.load(piece.getReceiptPath());
    }
}