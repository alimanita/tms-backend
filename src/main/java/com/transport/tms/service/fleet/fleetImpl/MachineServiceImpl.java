package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Machine;
import com.transport.tms.dto.fleet.request.MachineRequest;
import com.transport.tms.dto.fleet.request.UpdateHeuresRequest;
import com.transport.tms.dto.fleet.response.MachineResponse;
import com.transport.tms.dto.fleet.response.UpdateHeuresResponse;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.mapper.fleet.MachineMapper;
import com.transport.tms.repository.fleet.MachineRepository;
import com.transport.tms.service.fleet.MachineService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MachineServiceImpl implements MachineService {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;

    @Override
    public MachineResponse create(MachineRequest request) {
        log.info("Création d'une nouvelle machine: {}", request.nom());
        Machine machine = machineMapper.toEntity(request);
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toResponse(savedMachine);
    }

    @Override
    public MachineResponse update(Long id, MachineRequest request) {
        log.info("Mise à jour de la machine ID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        machineMapper.updateEntity(machine, request);
        Machine savedMachine = machineRepository.save(machine);
        return machineMapper.toResponse(savedMachine);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression (soft) de la machine ID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        machine.setActif(false);
        machineRepository.save(machine);
    }

    @Override
    @Transactional(readOnly = true)
    public MachineResponse getById(Long id) {
        log.info("Recherche de la machine ID: {}", id);
        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
        return machineMapper.toResponse(machine);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MachineResponse> getAll(Pageable pageable) {
        log.info("Récupération paginée de toutes les machines");
        return machineRepository.findAll(pageable)
                .map(machineMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MachineResponse> getAllActive() {
        log.info("Récupération de toutes les machines actives");
        return machineRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getActif()))
                .map(machineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UpdateHeuresResponse updateHeuresActuelles(Long id, UpdateHeuresRequest request) {
        log.info("Mise à jour des heures actuelles de la machine ID: {}", id);

        Machine machine = machineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        BigDecimal anciennesHeures = machine.getHeuresActuelles();

        if (anciennesHeures != null && request.heuresActuelles().compareTo(anciennesHeures) < 0) {
            throw new IllegalArgumentException(
                    "Les nouvelles heures (" + request.heuresActuelles()
                            + ") ne peuvent pas être inférieures aux heures actuelles (" + anciennesHeures + ")");
        }

        machine.setHeuresActuelles(request.heuresActuelles());
        Machine savedMachine = machineRepository.save(machine);

        return new UpdateHeuresResponse(
                savedMachine.getId(),
                savedMachine.getNom(),
                anciennesHeures,
                savedMachine.getHeuresActuelles(),
                LocalDateTime.now()
        );
    }
}
