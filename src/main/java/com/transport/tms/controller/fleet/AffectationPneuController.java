package com.transport.tms.controller.fleet;

import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.service.fleet.AffectationPneuService;
import lombok.RequiredArgsConstructor;
import com.transport.tms.controller.api.fleet.AffectationPneuApi;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import com.transport.tms.dto.fleet.response.PneuResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AffectationPneuController implements AffectationPneuApi {

    private final AffectationPneuService affectationPneuService;

    @Override
    public ResponseEntity<Page<PneuResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(affectationPneuService.findAll(pageable));
    }

    @Override
    public ResponseEntity<PneuResponse> findById(Long id) {
        return ResponseEntity.ok(affectationPneuService.findById(id));
    }

    @Override
    public ResponseEntity<PneuResponse> create(PneuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(affectationPneuService.create(request));
    }

    @Override
    public ResponseEntity<PneuResponse> update(Long id, PneuRequest request) {
        return ResponseEntity.ok(affectationPneuService.update(id, request));
    }

    @Override
    public ResponseEntity<List<PneuResponse>> findEnStock() {
        return ResponseEntity.ok(affectationPneuService.findEnStock());
    }

    @Override
    public ResponseEntity<Page<AffectationPneuResponse>> findAllAffectations(Pageable pageable) {
        return ResponseEntity.ok(affectationPneuService.findAllAffectations(pageable));
    }

    @Override
    public ResponseEntity<AffectationPneuResponse> affecter(AffectationPneuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(affectationPneuService.affecter(request));
    }

    @Override
    public ResponseEntity<AffectationPneuResponse> demonter(Long id,
                                                             BigDecimal unmountMileage,
                                                             AffectationPneu.RaisonDemontage raison) {
        return ResponseEntity.ok(affectationPneuService.demonter(id, unmountMileage, raison));
    }

    @Override
    public ResponseEntity<List<AffectationPneuResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(affectationPneuService.findByVehicule(vehiculeId));
    }
}