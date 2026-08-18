package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.PieceRechange;
import com.transport.tms.dto.fleet.request.PieceRechangeRequest;
import com.transport.tms.dto.fleet.response.PieceRechangeResponse;
import org.springframework.stereotype.Component;

@Component
public class PieceRechangeMapper {

    public PieceRechange toEntity(PieceRechangeRequest request) {
        PieceRechange piece = new PieceRechange();
        piece.setReference(request.reference());
        piece.setName(request.name());
        piece.setBrand(request.brand());
        piece.setUnit(request.unit() != null ? request.unit() : "PCS");
        piece.setUnitCost(request.unitCost());
        piece.setStockQty(request.stockQty() != null
                ? request.stockQty()
                : java.math.BigDecimal.ZERO);
        piece.setMinStockQty(request.minStockQty() != null
                ? request.minStockQty()
                : java.math.BigDecimal.ZERO);
        piece.setStockItemId(request.stockItemId());
        piece.setLocation(request.location());
        piece.setIsActive(true);
        piece.setAmountHT(request.amountHT());
        piece.setTvaRate(request.tvaRate());
        piece.setTvaAmount(request.tvaAmount());
        piece.setIsTvaRecoverable(request.isTvaRecoverable());
        piece.setRecoverableTvaAmount(request.recoverableTvaAmount());
        return piece;
    }

    public void updateEntity(PieceRechange piece, PieceRechangeRequest request) {
        piece.setName(request.name());
        piece.setBrand(request.brand());
        piece.setUnit(request.unit() != null ? request.unit() : "PCS");
        piece.setUnitCost(request.unitCost());
        piece.setMinStockQty(request.minStockQty() != null
                ? request.minStockQty()
                : java.math.BigDecimal.ZERO);
        piece.setStockItemId(request.stockItemId());
        piece.setLocation(request.location());
        piece.setAmountHT(request.amountHT());
        piece.setTvaRate(request.tvaRate());
        piece.setTvaAmount(request.tvaAmount());
        piece.setIsTvaRecoverable(request.isTvaRecoverable());
        piece.setRecoverableTvaAmount(request.recoverableTvaAmount());
    }

    public PieceRechangeResponse toResponse(PieceRechange piece) {
        return new PieceRechangeResponse(
                piece.getId(),
                piece.getReference(),
                piece.getName(),
                piece.getBrand(),
                piece.getUnit(),
                piece.getUnitCost(),
                piece.getStockQty(),
                piece.getMinStockQty(),
                piece.isLowStock(),
                piece.getLocation(),
                piece.getIsActive(),
                piece.getCreatedAt(),
                piece.getAmountHT(),
                piece.getTvaRate(),
                piece.getTvaAmount(),
                piece.getIsTvaRecoverable(),
                piece.getRecoverableTvaAmount(),
                piece.getReceiptPath()
        );
    }
}