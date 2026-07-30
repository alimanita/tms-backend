package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.rapport.*;
import com.transport.tms.repository.fleet.MachineRepository;
import com.transport.tms.repository.fleet.OrdreTravailRepository;
import com.transport.tms.repository.fleet.PleinCarburantRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.RapportEntretiensService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapportEntretiensServiceImpl implements RapportEntretiensService {

    private final OrdreTravailRepository ordreTravailRepository;
    private final PleinCarburantRepository pleinCarburantRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MachineRepository machineRepository;

    // ── Labels mois ───────────────────────────────────────────────────────────
    private static final String[] MOIS_LABELS = {
            "", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };

    // ── Entretiens par mois ───────────────────────────────────────────────────
    @Override
    public List<MaintenanceMensuelleDto> getRapportMensuel(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin) {

        LocalDateTime debutDt = debut.atStartOfDay();
        LocalDateTime finDt = fin.atTime(23, 59, 59);

        List<Object[]> rows = ordreTravailRepository.aggregateParMoisNative(
                entityType != null ? entityType.name() : null, debutDt, finDt);

        return rows.stream().map(r -> {
            int annee = ((Number) r[0]).intValue();
            int mois = ((Number) r[1]).intValue();
            return MaintenanceMensuelleDto.builder()
                    .annee(annee)
                    .mois(mois)
                    .moisLabel(MOIS_LABELS[mois])
                    .coutMainOeuvre((BigDecimal) r[2])
                    .coutPieces((BigDecimal) r[3])
                    .coutTotal((BigDecimal) r[4])
                    .nombreOT(((Number) r[5]).longValue())
                    .build();
        }).toList();
    }

    // ── Entretiens par année ──────────────────────────────────────────────────
    @Override
    public List<MaintenanceAnnuelleDto> getRapportAnnuel(
            OrdreTravail.TypeEntite entityType,
            int anDebut,
            int anFin) {

        List<Object[]> rows = ordreTravailRepository.aggregateParAnneeNative(
                entityType != null ? entityType.name() : null, anDebut, anFin);

        return rows.stream().map(r -> MaintenanceAnnuelleDto.builder()
                .annee(((Number) r[0]).intValue())
                .coutMainOeuvre((BigDecimal) r[1])
                .coutPieces((BigDecimal) r[2])
                .coutTotal((BigDecimal) r[3])
                .nombreOT(((Number) r[4]).longValue())
                .build()
        ).toList();
    }

    // ── Détail OT ─────────────────────────────────────────────────────────────
    @Override
    public List<MaintenanceDetailDto> getRapportDetail(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin) {

        LocalDateTime debutDt = debut.atStartOfDay();
        LocalDateTime finDt   = fin.atTime(23, 59, 59);

        List<OrdreTravail> ots =
                ordreTravailRepository.findDetailPourRapport(entityType, debutDt, finDt);

        return ots.stream().map(ot -> {
            String entityRef = resolveEntityRef(ot.getEntityType(), ot.getEntityId());

            return MaintenanceDetailDto.builder()
                    .id(ot.getId())
                    .reference(ot.getReference())
                    .entityRef(entityRef)
                    .entityType(ot.getEntityType())
                    .typeMaintenance(ot.getTypeMaintenance() != null ? ot.getTypeMaintenance().name() : null)
                    .priorite(ot.getPriorite() != null ? ot.getPriorite().name() : null)
                    .statut(ot.getStatut() != null ? ot.getStatut().name() : null)
                    .scheduledDate(ot.getScheduledDate())
                    .completedAt(ot.getCompletedAt())
                    .coutMainOeuvre(ot.getActualLaborCost())
                    .coutPieces(ot.getActualPartsCost())
                    .coutTotal(ot.getActualTotalCost())
                    .build();
        }).toList();
    }

    // ── Synthèse globale ──────────────────────────────────────────────────────
    @Override
    public SyntheseEntretiensDto getSynthese(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin) {

        LocalDateTime debutDt = debut.atStartOfDay();
        LocalDateTime finDt   = fin.atTime(23, 59, 59);

        BigDecimal coutMainOeuvre = ordreTravailRepository
                .sumMainOeuvrePourRapport(entityType, debutDt, finDt);
        BigDecimal coutPieces = ordreTravailRepository
                .sumPiecesPourRapport(entityType, debutDt, finDt);
        BigDecimal coutMaintenance = coutMainOeuvre.add(coutPieces);

        // Carburant (tous véhicules sur la période)
        List<CarburantMensuelDto> carburantMensuel =
                mapCarburantMensuel(pleinCarburantRepository.aggregateParMoisNative(null, debutDt, finDt));

        BigDecimal coutCarburant = carburantMensuel.stream()
                .map(CarburantMensuelDto::getCoutTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal litresTotaux = carburantMensuel.stream()
                .map(CarburantMensuelDto::getLitresTotaux)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long nombrePleins = carburantMensuel.stream()
                .mapToLong(CarburantMensuelDto::getNombrePleins)
                .sum();

        long nombreOT = ordreTravailRepository
                .findDetailPourRapport(entityType, debutDt, finDt)
                .size();

        return SyntheseEntretiensDto.builder()
                .coutTotalMaintenance(coutMaintenance)
                .coutMainOeuvreTotale(coutMainOeuvre)
                .coutPiecesTotales(coutPieces)
                .coutTotalCarburant(coutCarburant)
                .coutGlobal(coutMaintenance.add(coutCarburant))
                .nombreOT(nombreOT)
                .nombrePleins(nombrePleins)
                .litresTotaux(litresTotaux)
                .build();
    }

    // ── Carburant par mois ────────────────────────────────────────────────────
    @Override
    public List<CarburantMensuelDto> getCarburantMensuel(
            Long vehiculeId,
            LocalDate debut,
            LocalDate fin) {

        LocalDateTime debutDt = debut.atStartOfDay();
        LocalDateTime finDt   = fin.atTime(23, 59, 59);

        List<CarburantMensuelDto> data = mapCarburantMensuel(
                pleinCarburantRepository.aggregateParMoisNative(vehiculeId, debutDt, finDt));

        data.forEach(d -> d.setMoisLabel(MOIS_LABELS[d.getMois()] + " " + d.getAnnee()));
        return data;
    }

    // ── Carburant par année ───────────────────────────────────────────────────
    @Override
    public List<CarburantAnnuelDto> getCarburantAnnuel(
            Long vehiculeId,
            int anDebut,
            int anFin) {

        List<Object[]> rows = pleinCarburantRepository.aggregateParAnneeNative(vehiculeId, anDebut, anFin);

        return rows.stream().map(r -> CarburantAnnuelDto.builder()
                .annee(((Number) r[0]).intValue())
                .litresTotaux((BigDecimal) r[1])
                .coutTotal((BigDecimal) r[2])
                .nombrePleins(((Number) r[3]).longValue())
                .build()
        ).toList();
    }

    // ── Mapping helper carburant mensuel ────────────────────────────────────
    private List<CarburantMensuelDto> mapCarburantMensuel(List<Object[]> rows) {
        return rows.stream().map(r -> {
            int annee = ((Number) r[0]).intValue();
            int mois = ((Number) r[1]).intValue();
            return CarburantMensuelDto.builder()
                    .annee(annee)
                    .mois(mois)
                    .moisLabel(MOIS_LABELS[mois])
                    .litresTotaux((BigDecimal) r[2])
                    .coutTotal((BigDecimal) r[3])
                    .nombrePleins(((Number) r[4]).longValue())
                    .consommationMoyenne(r[5] != null ? BigDecimal.valueOf(((Number) r[5]).doubleValue()) : null)
                    .build();
        }).toList();
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private String resolveEntityRef(OrdreTravail.TypeEntite type, Long id) {
        if (type == null || id == null) return "—";
        try {
            if (type == OrdreTravail.TypeEntite.VEHICLE) {
                return vehiculeRepository.findById(id)
                        .map(v -> v.getImmatriculation() != null
                                ? v.getImmatriculation()
                                : v.getReference())
                        .orElse("Véhicule #" + id);
            } else {
                return machineRepository.findById(id)
                        .map(m -> m.getNom() != null ? m.getNom() : m.getReference())
                        .orElse("Machine #" + id);
            }
        } catch (Exception e) {
            log.warn("Impossible de résoudre l'entité {} #{}", type, id);
            return type.name() + " #" + id;
        }
    }
}