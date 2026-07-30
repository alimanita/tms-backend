package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.ChangementHuile;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.dto.fleet.request.ChangementHuileRequest;
import com.transport.tms.dto.fleet.response.ChangementHuileResponse;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.mapper.fleet.ChangementHuileMapper;
import com.transport.tms.repository.fleet.ChangementHuileRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.ChangementHuileService;
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
public class ChangementHuileServiceImpl implements ChangementHuileService {

    private final ChangementHuileRepository changementHuileRepository;
    private final ChangementHuileMapper changementHuileMapper;
    private final VehiculeRepository vehiculeRepository;
    // private final MachineRepository machineRepository; // à ajouter si TypeEntite.MACHINE doit résoudre une référence

    @Override
    @Transactional(readOnly = true)
    public Page<ChangementHuileResponse> findAll(Pageable pageable) {
        return changementHuileRepository.findAll(pageable)
                .map(ch -> changementHuileMapper.toResponse(ch, resolveEntityRef(ch)));
    }

    @Override
    @Transactional(readOnly = true)
    public ChangementHuileResponse findById(Long id) {
        ChangementHuile ch = getOrThrow(id);
        return changementHuileMapper.toResponse(ch, resolveEntityRef(ch));
    }

    @Override
    public ChangementHuileResponse create(ChangementHuileRequest request) {
        ChangementHuile ch = changementHuileMapper.toEntity(request);
        ch.setReference(generateReference());
        ChangementHuile saved = changementHuileRepository.save(ch);
        log.info("Changement d'huile créé : {}", saved.getReference());
        return changementHuileMapper.toResponse(saved, resolveEntityRef(saved));
    }

    @Override
    public ChangementHuileResponse update(Long id, ChangementHuileRequest request) {
        ChangementHuile ch = getOrThrow(id);
        changementHuileMapper.updateEntity(ch, request);
        ChangementHuile saved = changementHuileRepository.save(ch);
        return changementHuileMapper.toResponse(saved, resolveEntityRef(saved));
    }

    @Override
    public void delete(Long id) {
        ChangementHuile ch = getOrThrow(id);
        changementHuileRepository.delete(ch);
        log.info("Changement d'huile {} supprimé", ch.getReference());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChangementHuileResponse> findByVehicule(Long vehiculeId) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        return changementHuileRepository
                .findByEntityTypeAndEntityIdOrderByChangeDateDesc(ChangementHuile.TypeEntite.VEHICLE, vehiculeId)
                .stream()
                .map(ch -> changementHuileMapper.toResponse(ch, vehicule.getReference()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChangementHuileResponse> findByMachine(Long machineId) {
        return changementHuileRepository
                .findByEntityTypeAndEntityIdOrderByChangeDateDesc(ChangementHuile.TypeEntite.MACHINE, machineId)
                .stream()
                .map(ch -> changementHuileMapper.toResponse(ch, null)) // TODO: résoudre la référence machine si entité Machine existe
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChangementHuileResponse> findAVenir() {
        LocalDate limite = LocalDate.now().plusDays(30);

        // Note : la méthode repository combine date ET km via un OR global (pas par véhicule).
        // On passe BigDecimal.ZERO pour le km afin de neutraliser la condition kilométrique ici
        // et ne garder que le filtre par date ; à affiner si une logique par véhicule est nécessaire.
        return changementHuileRepository.findAVenir(limite, BigDecimal.ZERO)
                .stream()
                .map(ch -> changementHuileMapper.toResponse(ch, resolveEntityRef(ch)))
                .collect(Collectors.toList());
    }

    private String resolveEntityRef(ChangementHuile ch) {
        if (ch.getEntityType() == ChangementHuile.TypeEntite.VEHICLE) {
            return vehiculeRepository.findById(ch.getEntityId())
                    .map(Vehicule::getReference)
                    .orElse(null);
        }
        // Si TypeEntite.MACHINE existe avec une entité Machine, résoudre ici
        return null;
    }

    private ChangementHuile getOrThrow(Long id) {
        return changementHuileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
    }

    private String generateReference() {
        long count = changementHuileRepository.count() + 1;
        return "CH-" + java.time.Year.now().getValue() + "-" + String.format("%04d", count);
    }
}