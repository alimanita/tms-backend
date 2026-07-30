package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.VehiculeMapper;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.VehiculeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeResponse create(VehiculeRequest request) {
        // Générer référence automatiquement si absente
        String reference = (request.reference() != null && !request.reference().isBlank())
                ? request.reference()
                : genererReference();

        if (vehiculeRepository.existsByReference(reference)) {
            reference = genererReference(); // retry
        }
        if (vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new InvalidOperationException(
                    "Un véhicule avec l'immatriculation '" + request.immatriculation() + "' existe déjà");
        }

        Vehicule vehicule = vehiculeMapper.toEntity(request);
        vehicule.setReference(reference);  // ← forcer la référence générée

        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    private String genererReference() {
        int annee = LocalDate.now().getYear();
        long count = vehiculeRepository.count() + 1;
        return String.format("VH-%d-%03d", annee, count);  // VH-2026-001
    }
    @Override
    public VehiculeResponse update(Long id, VehiculeRequest request) {
        Vehicule vehicule = findEntityById(id);
        if (vehicule.getStatut() == StatutVehicule.HS) {
            throw new InvalidOperationException(
                    "Un véhicule hors service ne peut pas être modifié");
        }
        vehiculeMapper.updateEntity(vehicule, request);
        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeResponse findById(Long id) {
        return vehiculeMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculeResponse> findAll(Pageable pageable) {
        return vehiculeRepository.findByActifTrue(pageable)
                .map(vehiculeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeResponse> findDisponibles() {
        return vehiculeRepository
                .findByStatutAndActifTrue(StatutVehicule.DISPONIBLE)
                .stream()
                .map(vehiculeMapper::toResponse)
                .toList();
    }

    @Override
    public VehiculeResponse updateStatut(Long id, String statut) {
        Vehicule vehicule = findEntityById(id);
        StatutVehicule nouveauStatut;
        try {
            nouveauStatut = StatutVehicule.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException(
                    "Statut invalide : " + statut
                            + ". Valeurs acceptées : DISPONIBLE, EN_MISSION, EN_MAINTENANCE, HS");
        }
        vehicule.setStatut(nouveauStatut);
        return vehiculeMapper.toResponse(vehiculeRepository.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = findEntityById(id);
        if (vehicule.getStatut() == StatutVehicule.EN_MISSION) {
            throw new InvalidOperationException(
                    "Impossible de désactiver un véhicule en mission");
        }
        vehicule.setActif(false);   // ✅ setActif au lieu de setIsActive
        vehiculeRepository.save(vehicule);
    }

    private Vehicule findEntityById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Véhicule introuvable avec l'ID = " + id));
    }
}