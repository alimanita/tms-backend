package com.transport.tms.service.fleet;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.request.OTMainOeuvreRequest;
import com.transport.tms.dto.fleet.request.OTPieceRechangeRequest;
import com.transport.tms.dto.fleet.request.OrdreTravailRequest;
import com.transport.tms.dto.fleet.response.OrdreTravailResponse;
import com.transport.tms.dto.fleet.response.StatsSyageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
public interface OrdreTravailService {
    OrdreTravailResponse create(OrdreTravailRequest request);
    OrdreTravailResponse update(Long id, OrdreTravailRequest request);
    OrdreTravailResponse findById(Long id);
    Page<OrdreTravailResponse> findAll(Pageable pageable);
    List<OrdreTravailResponse> findByVehicule(Long vehiculeId);
    List<OrdreTravailResponse> findByMachine(Long machineId);
    List<OrdreTravailResponse> findAVenir();
    OrdreTravailResponse demarrer(Long id);
    OrdreTravailResponse cloturer(Long id);
    OrdreTravailResponse annuler(Long id);
    OrdreTravailResponse addPiece(Long id, OTPieceRechangeRequest request);
    OrdreTravailResponse removePiece(Long id, Long pieceId);
    OrdreTravailResponse addMainOeuvre(Long id, OTMainOeuvreRequest request);
    OrdreTravailResponse removeMainOeuvre(Long id, Long mainOeuvreId);


    /** Historique de tous les changements de lames avec leurs stats. */
    List<StatsSyageResponse> getHistoriqueLamesMachine(Long machineId, Integer idEntreprise);


    Page<OrdreTravailResponse> findAll(Pageable pageable, OrdreTravail.StatutOT statut,
                                       OrdreTravail.TypeEntite entityType, String search,
                                       LocalDate dateDebut, LocalDate dateFin);

}