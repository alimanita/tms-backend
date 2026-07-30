package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import org.springframework.stereotype.Component;

@Component
public class AffectationPneuMapper {

    public AffectationPneu toEntity(AffectationPneuRequest request) {
        AffectationPneu affectation = new AffectationPneu();
        affectation.setPosition(request.position());
        affectation.setMountDate(request.mountDate());
        affectation.setMountMileage(request.mountMileage());
        affectation.setUnmountDate(request.unmountDate());
        affectation.setUnmountMileage(request.unmountMileage());
        affectation.setReasonUnmount(request.reasonUnmount());
        affectation.setNotes(request.notes());
        // pneu et vehicule sont résolus et assignés dans le service
        // (besoin d'aller chercher les entités via leurs repositories)
        return affectation;
    }

    public AffectationPneuResponse toResponse(AffectationPneu affectation) {
        return new AffectationPneuResponse(
                affectation.getId(),
                affectation.getPneu() != null ? affectation.getPneu().getId() : null,
                affectation.getPneu() != null ? affectation.getPneu().getSerialNumber() : null,
                affectation.getPneu() != null ? affectation.getPneu().getBrand() : null,
                affectation.getPneu() != null ? affectation.getPneu().getSize() : null,
                affectation.getVehicule() != null ? affectation.getVehicule().getId() : null,
                affectation.getVehicule() != null ? affectation.getVehicule().getReference() : null,
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