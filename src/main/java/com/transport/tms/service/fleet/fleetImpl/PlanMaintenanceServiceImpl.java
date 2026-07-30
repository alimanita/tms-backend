package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.PlanMaintenance;
import com.transport.tms.dto.fleet.request.PlanMaintenanceRequest;
import com.transport.tms.dto.fleet.response.PlanMaintenanceResponse;
import com.transport.tms.mapper.fleet.PlanMaintenanceMapper;
import com.transport.tms.repository.fleet.PlanMaintenanceRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.PlanMaintenanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanMaintenanceServiceImpl implements PlanMaintenanceService {

    private final PlanMaintenanceRepository planRepository;
    private final VehiculeRepository vehiculeRepository;
    private final PlanMaintenanceMapper mapper;

    // ── Nombre de jours avant échéance considéré "proche" ──────
    private static final int JOURS_ALERTE = 30;

    @Override
    @Transactional(readOnly = true)
    public Page<PlanMaintenanceResponse> findAll(Pageable pageable) {
        return planRepository.findAll(pageable)
                .map(p -> mapper.toResponse(p, resolveEntityRef(p)));
    }

    @Override
    @Transactional(readOnly = true)
    public PlanMaintenanceResponse findById(Long id) {
        PlanMaintenance plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan introuvable : " + id));
        return mapper.toResponse(plan, resolveEntityRef(plan));
    }

    @Override
    public PlanMaintenanceResponse create(PlanMaintenanceRequest request) {
        PlanMaintenance plan = mapper.toEntity(request);
        return mapper.toResponse(planRepository.save(plan), resolveEntityRef(plan));
    }

    @Override
    public PlanMaintenanceResponse update(Long id, PlanMaintenanceRequest request) {
        PlanMaintenance plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan introuvable : " + id));
        mapper.updateEntity(plan, request);
        return mapper.toResponse(planRepository.save(plan), resolveEntityRef(plan));
    }

    @Override
    public void delete(Long id) {
        PlanMaintenance plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan introuvable : " + id));
        // soft delete
        plan.setIsActive(false);
        planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanMaintenanceResponse> findByVehicule(Long vehiculeId) {
        return planRepository
                .findByEntityTypeAndEntityIdAndIsActiveTrue(
                        PlanMaintenance.TypeEntite.VEHICLE, vehiculeId)
                .stream()
                .map(p -> mapper.toResponse(p, resolveEntityRef(p)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanMaintenanceResponse> findByMachine(Long machineId) {
        return planRepository
                .findByEntityTypeAndEntityIdAndIsActiveTrue(
                        PlanMaintenance.TypeEntite.MACHINE, machineId)
                .stream()
                .map(p -> mapper.toResponse(p, resolveEntityRef(p)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanMaintenanceResponse> findEcheancesProches() {
        LocalDate limitDate = LocalDate.now().plusDays(JOURS_ALERTE);

        // Plans calendrier proches
        List<PlanMaintenance> calendrier =
                planRepository.findEcheancesCalendrierProches(limitDate);

        // Plans km : on récupère le km moyen de tous les véhicules actifs
        // comme valeur de référence globale pour l'alerte
        java.math.BigDecimal kmMoyen = vehiculeRepository.findAll()
                .stream()
                .filter(v -> v.getKilometrageActuel() != null)
                .map(v -> v.getKilometrageActuel())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        List<PlanMaintenance> km =
                planRepository.findEcheancesKmAtteintes(kmMoyen);

        // Fusionner en dédupliquant par id
        return Stream.concat(calendrier.stream(), km.stream())
                .distinct()
                .map(p -> mapper.toResponse(p, resolveEntityRef(p)))
                .toList();
    }

    // ── Résolution de la référence lisible de l'entité ──────────

    private String resolveEntityRef(PlanMaintenance plan) {
        if (plan.getEntityType() == null || plan.getEntityId() == null) return "";

        return switch (plan.getEntityType()) {
            case VEHICLE -> vehiculeRepository.findById(plan.getEntityId())
                    .map(v -> v.getImmatriculation() != null
                            ? v.getImmatriculation()
                            : v.getReference())
                    .orElse("VH-" + plan.getEntityId());
            case MACHINE  -> "MACHINE-" + plan.getEntityId(); // à adapter si tu as un MachineRepository
            default       -> String.valueOf(plan.getEntityId());
        };
    }
}