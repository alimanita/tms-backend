package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Pneu;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.PneuResponse;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.mapper.fleet.PneuMapper;
import com.transport.tms.repository.fleet.PneuRepository;
import com.transport.tms.service.fleet.PneuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PneuServiceImpl implements PneuService {

    private final PneuRepository pneuRepository;
    private final PneuMapper pneuMapper;

    @Override
    public PneuResponse create(PneuRequest request) {
        log.info("Création d'un nouveau pneu: {}", request.serialNumber());
        Pneu pneu = pneuMapper.toEntity(request);
        Pneu savedPneu = pneuRepository.save(pneu);
        return pneuMapper.toResponse(savedPneu);
    }

    @Override
    public PneuResponse update(Long id, PneuRequest request) {
        log.info("Mise à jour du pneu ID: {}", id);
        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        pneuMapper.updateEntity(pneu, request);
        Pneu savedPneu = pneuRepository.save(pneu);
        return pneuMapper.toResponse(savedPneu);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression (soft) du pneu ID: {}", id);
        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        pneu.setIsActive(false);
        pneuRepository.save(pneu);
    }

    @Override
    @Transactional(readOnly = true)
    public PneuResponse getById(Long id) {
        log.info("Recherche du pneu ID: {}", id);
        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
        return pneuMapper.toResponse(pneu);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PneuResponse> getAll(Pageable pageable) {
        log.info("Récupération paginée de tous les pneus");
        return pneuRepository.findAll(pageable)
                .map(pneuMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PneuResponse> getAllActive() {
        log.info("Récupération de tous les pneus actifs");
        return pneuRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .map(pneuMapper::toResponse)
                .collect(Collectors.toList());
    }
}
