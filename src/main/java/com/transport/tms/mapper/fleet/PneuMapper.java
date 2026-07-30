package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.domain.entity.fleet.Pneu;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import com.transport.tms.dto.fleet.response.PneuResponse;
import org.springframework.stereotype.Component;

@Component
public class PneuMapper {

    // ── Pneu ──────────────────────────────────────────────────

    public Pneu toEntity(PneuRequest request) {
        Pneu pneu = new Pneu();
        pneu.setSerialNumber(request.serialNumber());
        pneu.setBrand(request.brand());
        pneu.setModel(request.model());
        pneu.setSize(request.size());
        pneu.setType(request.type());
        pneu.setPurchaseDate(request.purchaseDate());
        pneu.setPurchaseCost(request.purchaseCost());
        pneu.setMaxKm(request.maxKm());
        pneu.setStatus(Pneu.StatutPneu.STOCK);
        pneu.setIsActive(true);
        return pneu;
    }

    public void updateEntity(Pneu pneu, PneuRequest request) {
        pneu.setBrand(request.brand());
        pneu.setModel(request.model());
        pneu.setSize(request.size());
        pneu.setType(request.type());
        pneu.setPurchaseDate(request.purchaseDate());
        pneu.setPurchaseCost(request.purchaseCost());
        pneu.setMaxKm(request.maxKm());
    }

    public PneuResponse toResponse(Pneu pneu) {
        return new PneuResponse(
                pneu.getId(),
                pneu.getSerialNumber(),
                pneu.getBrand(),
                pneu.getModel(),
                pneu.getSize(),
                pneu.getType(),
                pneu.getPurchaseDate(),
                pneu.getPurchaseCost(),
                pneu.getMaxKm(),
                pneu.getStatus(),
                pneu.getIsActive(),
                pneu.getCreatedAt()
        );
    }

    // ── Affectation ───────────────────────────────────────────

    public AffectationPneu toAffectationEntity(AffectationPneuRequest request) {
        AffectationPneu affectation = new AffectationPneu();
        affectation.setPosition(request.position());
        affectation.setMountDate(request.mountDate());
        affectation.setMountMileage(request.mountMileage());
        affectation.setNotes(request.notes());
        return affectation;
    }

    public AffectationPneuResponse toAffectationResponse(AffectationPneu affectation) {
        return new AffectationPneuResponse(
                affectation.getId(),
                affectation.getPneu().getId(),
                affectation.getPneu().getSerialNumber(),
                affectation.getPneu().getBrand(),
                affectation.getPneu().getSize(),
                affectation.getVehicule().getId(),
                affectation.getVehicule().getReference(),
                affectation.getPosition(),
                affectation.getMountDate(),
                affectation.getMountMileage(),
                affectation.getUnmountDate(),
                affectation.getUnmountMileage(),
                affectation.getKmUsed(),
                affectation.getReasonUnmount(),
                affectation.getNotes(),
                affectation.getCreatedAt()
        );
    }
}