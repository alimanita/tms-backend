package com.transport.tms.controller.api.fleet;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.request.OTMainOeuvreRequest;
import jakarta.validation.Valid;

import com.transport.tms.dto.fleet.request.OTPieceRechangeRequest;
import com.transport.tms.dto.fleet.request.OrdreTravailRequest;
import com.transport.tms.dto.fleet.response.OrdreTravailResponse;
import com.transport.tms.dto.fleet.response.StatsSyageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RequestMapping("api/v1/fleet/ordres-travail")
public interface OrdreTravailApi {


    @GetMapping("/{id}")
    ResponseEntity<OrdreTravailResponse> findById(@PathVariable Long id);

    @PostMapping
    ResponseEntity<OrdreTravailResponse> create(@Valid @RequestBody OrdreTravailRequest request);

    @PutMapping("/{id}")
    ResponseEntity<OrdreTravailResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody OrdreTravailRequest request);

    @PatchMapping("/{id}/demarrer")
    ResponseEntity<OrdreTravailResponse> demarrer(@PathVariable Long id);

    @PatchMapping("/{id}/cloturer")
    ResponseEntity<OrdreTravailResponse> cloturer(@PathVariable Long id);

    @PatchMapping("/{id}/annuler")
    ResponseEntity<OrdreTravailResponse> annuler(@PathVariable Long id);

    @PostMapping("/{id}/pieces")
    ResponseEntity<OrdreTravailResponse> addPiece(@PathVariable Long id,
                                                  @Valid @RequestBody OTPieceRechangeRequest request);

    @DeleteMapping("/{id}/pieces/{pieceId}")
    ResponseEntity<OrdreTravailResponse> removePiece(@PathVariable Long id, @PathVariable Long pieceId);

    @PostMapping("/{id}/main-oeuvre")
    ResponseEntity<OrdreTravailResponse> addMainOeuvre(@PathVariable Long id,
                                                       @Valid @RequestBody OTMainOeuvreRequest request);

    @DeleteMapping("/{id}/main-oeuvre/{mainOeuvreId}")
    ResponseEntity<OrdreTravailResponse> removeMainOeuvre(@PathVariable Long id, @PathVariable Long mainOeuvreId);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<OrdreTravailResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/machine/{machineId}")
    ResponseEntity<List<OrdreTravailResponse>> findByMachine(@PathVariable Long machineId);

    @GetMapping("/a-venir")
    ResponseEntity<List<OrdreTravailResponse>> findAVenir();



    /**
     * Historique de tous les changements de lames avec leurs stats pour une machine.
     */
    @GetMapping("/machine/{machineId}/historique-lames")
    ResponseEntity<List<StatsSyageResponse>> getHistoriqueLamesMachine(
            @PathVariable Long machineId,
            @RequestParam Integer idEntreprise);

    @GetMapping
    ResponseEntity<Page<OrdreTravailResponse>> findAll(
            Pageable pageable,
            @RequestParam(required = false) OrdreTravail.StatutOT statut,
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin);

}