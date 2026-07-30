package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.PieceRechangeRequest;
import com.transport.tms.dto.fleet.response.PieceRechangeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PieceRechangeService {
    PieceRechangeResponse create(PieceRechangeRequest request);
    PieceRechangeResponse update(Long id, PieceRechangeRequest request);
    PieceRechangeResponse findById(Long id);
    Page<PieceRechangeResponse> findAll(Pageable pageable);
    List<PieceRechangeResponse> findStockFaible();
    PieceRechangeResponse updateStock(Long id, BigDecimal quantite);
    void delete(Long id);
}