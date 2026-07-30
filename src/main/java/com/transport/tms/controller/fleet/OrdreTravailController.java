package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.OrdreTravailApi;
import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.request.OTMainOeuvreRequest;
import com.transport.tms.dto.fleet.request.OTPieceRechangeRequest;
import com.transport.tms.dto.fleet.request.OrdreTravailRequest;
import com.transport.tms.dto.fleet.response.OrdreTravailResponse;
import com.transport.tms.dto.fleet.response.StatsSyageResponse;
import com.transport.tms.service.fleet.OrdreTravailService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrdreTravailController implements OrdreTravailApi {

    private final OrdreTravailService ordreTravailService;


    @Override
    public ResponseEntity<OrdreTravailResponse> findById(Long id) {
        return ResponseEntity.ok(ordreTravailService.findById(id));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> create(OrdreTravailRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordreTravailService.create(request));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> update(Long id, OrdreTravailRequest request) {
        return ResponseEntity.ok(ordreTravailService.update(id, request));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> demarrer(Long id) {
        return ResponseEntity.ok(ordreTravailService.demarrer(id));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> cloturer(Long id) {
        return ResponseEntity.ok(ordreTravailService.cloturer(id));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> annuler(Long id) {
        return ResponseEntity.ok(ordreTravailService.annuler(id));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> addPiece(Long id,
                                                         OTPieceRechangeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordreTravailService.addPiece(id, request));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> removePiece(Long id, Long pieceId) {
        return ResponseEntity.ok(ordreTravailService.removePiece(id, pieceId));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> addMainOeuvre(Long id, OTMainOeuvreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordreTravailService.addMainOeuvre(id, request));
    }

    @Override
    public ResponseEntity<OrdreTravailResponse> removeMainOeuvre(Long id, Long mainOeuvreId) {
        return ResponseEntity.ok(ordreTravailService.removeMainOeuvre(id, mainOeuvreId));
    }

    @Override
    public ResponseEntity<List<OrdreTravailResponse>> findByVehicule(Long vehiculeId) {
        return ResponseEntity.ok(ordreTravailService.findByVehicule(vehiculeId));
    }

    @Override
    public ResponseEntity<List<OrdreTravailResponse>> findByMachine(Long machineId) {
        return ResponseEntity.ok(ordreTravailService.findByMachine(machineId));
    }

    @Override
    public ResponseEntity<List<OrdreTravailResponse>> findAVenir() {
        return ResponseEntity.ok(ordreTravailService.findAVenir());
    }



    @Override
    public ResponseEntity<List<StatsSyageResponse>> getHistoriqueLamesMachine(Long machineId, Integer idEntreprise) {
        return ResponseEntity.ok(ordreTravailService.getHistoriqueLamesMachine(machineId, idEntreprise));
    }
    @Override
    public ResponseEntity<Page<OrdreTravailResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) OrdreTravail.StatutOT statut,
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(ordreTravailService.findAll(pageable, statut, entityType, search, dateDebut, dateFin));
    }
}