package com.transport.tms.mapper;

import com.transport.tms.domain.entity.PieceRechange;
import com.transport.tms.dto.request.PieceRechangeRequest;
import com.transport.tms.dto.response.PieceRechangeResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PieceRechangeMapper {

    public PieceRechange toEntity(PieceRechangeRequest request) {
        PieceRechange piece = new PieceRechange();
        piece.setReference(request.reference());
        piece.setName(request.name());
        piece.setCategory(request.category());
        piece.setSupplier(request.supplier());
        piece.setUnitCost(request.unitCost());
        piece.setStockQty(request.stockQty() != null
                ? request.stockQty()
                : BigDecimal.ZERO);
        piece.setMinStockQty(request.minStockQty() != null
                ? request.minStockQty()
                : BigDecimal.ZERO);
        piece.setIsActive(true);
        return piece;
    }

    public void updateEntity(PieceRechange piece, PieceRechangeRequest request) {
        piece.setName(request.name());
        piece.setCategory(request.category());
        piece.setSupplier(request.supplier());
        piece.setUnitCost(request.unitCost());
        piece.setMinStockQty(request.minStockQty() != null
                ? request.minStockQty()
                : BigDecimal.ZERO);
    }

    public PieceRechangeResponse toResponse(PieceRechange piece) {
        return new PieceRechangeResponse(
                piece.getId(),
                piece.getReference(),
                piece.getName(),
                piece.getCategory(),
                piece.getSupplier(),
                piece.getUnitCost(),
                piece.getStockQty(),
                piece.getMinStockQty(),
                piece.isLowStock(),
                piece.getIsActive()
        );
    }
}