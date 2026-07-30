package com.transport.tms.service.fleet.fleetImpl;


import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.domain.entity.fleet.Pneu;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import com.transport.tms.dto.fleet.response.PneuResponse;
import com.transport.tms.exception.ErrorCodes;

import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.AffectationPneuMapper;
import com.transport.tms.mapper.fleet.PneuMapper;
import com.transport.tms.repository.fleet.AffectationPneuRepository;
import com.transport.tms.repository.fleet.PneuRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.AffectationPneuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AffectationPneuServiceImpl implements AffectationPneuService {

    private final PneuRepository pneuRepository;
    private final PneuMapper pneuMapper;
    private final AffectationPneuRepository affectationPneuRepository;
    private final AffectationPneuMapper affectationPneuMapper;
    private final VehiculeRepository vehiculeRepository;

    // ── Pneus ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<PneuResponse> findAll(Pageable pageable) {
        return pneuRepository.findAll(pageable).map(pneuMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PneuResponse findById(Long id) {
        Pneu pneu = getPneuOrThrow(id);
        return pneuMapper.toResponse(pneu);
    }

    @Override
    public PneuResponse create(PneuRequest request) {
        Pneu pneu = pneuMapper.toEntity(request);
        pneu.setStatus(Pneu.StatutPneu.STOCK);
        pneu.setIsActive(true);
        Pneu saved = pneuRepository.save(pneu);
        log.info("Pneu créé : {}", saved.getId());
        return pneuMapper.toResponse(saved);
    }

    @Override
    public PneuResponse update(Long id, PneuRequest request) {
        Pneu pneu = getPneuOrThrow(id);
        pneuMapper.updateEntity(pneu, request);
        return pneuMapper.toResponse(pneuRepository.save(pneu));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PneuResponse> findEnStock() {
        return pneuRepository.findByStatusAndIsActiveTrueOrderByPurchaseDateAsc(Pneu.StatutPneu.STOCK)
                .stream()
                .map(pneuMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── Affectations ───────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<AffectationPneuResponse> findAllAffectations(Pageable pageable) {
        return affectationPneuRepository.findAll(pageable).map(affectationPneuMapper::toResponse);
    }

    @Override
    public AffectationPneuResponse affecter(AffectationPneuRequest request) {
        Pneu pneu = getPneuOrThrow(request.pneuId());

        if (pneu.getStatus() != Pneu.StatutPneu.STOCK) {
            throw new InvalidOperationException(
                    "Ce pneu n'est pas disponible en stock (statut actuel : " + pneu.getStatus() + ")",
                    ErrorCodes.PNEU_NOT_AVAILABLE
            );
        }

        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        AffectationPneu affectation = affectationPneuMapper.toEntity(request);
        affectation.setPneu(pneu);
        affectation.setVehicule(vehicule);

        AffectationPneu saved = affectationPneuRepository.save(affectation);

        pneu.setStatus(Pneu.StatutPneu.MOUNTED);
        pneuRepository.save(pneu);

        log.info("Pneu {} affecté au véhicule {}", pneu.getId(), vehicule.getReference());
        return affectationPneuMapper.toResponse(saved);
    }

    @Override
    public AffectationPneuResponse demonter(Long id, BigDecimal unmountMileage, AffectationPneu.RaisonDemontage raison) {
        AffectationPneu affectation = affectationPneuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        affectation.setUnmountDate(LocalDate.now());
        affectation.setUnmountMileage(unmountMileage);
        affectation.setReasonUnmount(raison);

        AffectationPneu saved = affectationPneuRepository.save(affectation);

        Pneu pneu = affectation.getPneu();
        pneu.setStatus(raison == AffectationPneu.RaisonDemontage.DAMAGED
                ? Pneu.StatutPneu.SCRAP
                : Pneu.StatutPneu.STOCK);
        pneuRepository.save(pneu);

        log.info("Pneu {} démonté du véhicule (raison : {})", pneu.getId(), raison);
        return affectationPneuMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffectationPneuResponse> findByVehicule(Long vehiculeId) {
        return affectationPneuRepository.findByVehiculeIdOrderByMountDateDesc(vehiculeId)
                .stream()
                .map(affectationPneuMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Pneu getPneuOrThrow(Long id) {
        return pneuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
    }
}