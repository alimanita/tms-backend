package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.*;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.request.OTMainOeuvreRequest;
import com.transport.tms.dto.fleet.request.OTPieceRechangeRequest;
import com.transport.tms.dto.fleet.request.OrdreTravailRequest;
import com.transport.tms.dto.fleet.response.OrdreTravailResponse;
import com.transport.tms.dto.fleet.response.StatsSyageResponse;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.mapper.fleet.OrdreTravailMapper;
import com.transport.tms.repository.fleet.MachineRepository;
import com.transport.tms.repository.fleet.OrdreTravailRepository;
import com.transport.tms.repository.fleet.PieceRechangeRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.service.fleet.OrdreTravailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrdreTravailServiceImpl implements OrdreTravailService {

    private final OrdreTravailRepository ordreTravailRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MachineRepository machineRepository;
    private final PieceRechangeRepository pieceRechangeRepository;

    private final OrdreTravailMapper mapper;

    @Override
    public OrdreTravailResponse create(OrdreTravailRequest request) {
        // Vérifier que l'entité existe
        String entityRef = resolveEntityRef(request.entityType(), request.entityId());

        // Mettre le véhicule en maintenance si applicable
        if (request.entityType() == OrdreTravail.TypeEntite.VEHICLE) {
            Vehicule vehicule = findVehiculeById(request.entityId());
            if (vehicule.getStatut() == StatutVehicule.EN_MISSION) {
                throw new InvalidOperationException(
                        "Impossible de créer un OT pour un véhicule en mission"
                );
            }
            vehicule.setStatut(StatutVehicule.EN_MAINTENANCE);
            vehiculeRepository.save(vehicule);
        }

        OrdreTravail ordre = mapper.toEntity(request);
        ordre.setReference(generateReference());

        OrdreTravail saved = ordreTravailRepository.save(ordre);
        log.info("OT créé : {}", saved.getReference());
        return mapper.toResponse(saved, entityRef);
    }

    @Override
    public OrdreTravailResponse update(Long id, OrdreTravailRequest request) {
        OrdreTravail ordre = findEntityById(id);
        if (ordre.getStatut() == OrdreTravail.StatutOT.COMPLETED
                || ordre.getStatut() == OrdreTravail.StatutOT.CANCELLED) {
            throw new InvalidOperationException(
                    "Un OT clôturé ou annulé ne peut pas être modifié"
            );
        }
        mapper.updateEntity(ordre, request);
        String entityRef = resolveEntityRef(ordre.getEntityType(), ordre.getEntityId());
        return mapper.toResponse(ordreTravailRepository.save(ordre), entityRef);
    }

    @Override
    @Transactional(readOnly = true)
    public OrdreTravailResponse findById(Long id) {
        OrdreTravail ordre = findEntityById(id);
        String entityRef = resolveEntityRef(ordre.getEntityType(), ordre.getEntityId());
        return mapper.toResponse(ordre, entityRef);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdreTravailResponse> findAll(Pageable pageable) {
        return ordreTravailRepository.findAll(pageable)
                .map(o -> mapper.toResponse(o,
                        resolveEntityRef(o.getEntityType(), o.getEntityId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdreTravailResponse> findByVehicule(Long vehiculeId) {
        return ordreTravailRepository
                .findByEntityTypeAndEntityId(OrdreTravail.TypeEntite.VEHICLE, vehiculeId)
                .stream()
                .map(o -> mapper.toResponse(o, resolveEntityRef(o.getEntityType(), o.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdreTravailResponse> findByMachine(Long machineId) {
        return ordreTravailRepository
                .findByEntityTypeAndEntityId(OrdreTravail.TypeEntite.MACHINE, machineId)
                .stream()
                .map(o -> mapper.toResponse(o, resolveEntityRef(o.getEntityType(), o.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdreTravailResponse> findAVenir() {
        return ordreTravailRepository
                .findAVenir(LocalDate.now(), LocalDate.now().plusDays(30))
                .stream()
                .map(o -> mapper.toResponse(o, resolveEntityRef(o.getEntityType(), o.getEntityId())))
                .toList();
    }

    @Override
    public OrdreTravailResponse demarrer(Long id) {
        OrdreTravail ordre = findEntityById(id);
        ordre.demarrer(); // logique métier dans l'entité
        return mapper.toResponse(ordreTravailRepository.save(ordre),
                resolveEntityRef(ordre.getEntityType(), ordre.getEntityId()));
    }
    @Override
    public OrdreTravailResponse annuler(Long id) {
        OrdreTravail ordre = findEntityById(id);
        ordre.annuler();

        // Restocke toutes les pièces réservées par cet OT
        for (OTPieceRechange otPiece : ordre.getPieces()) {
            BigDecimal quantiteEffective = otPiece.getQuantityUsed() != null
                    ? otPiece.getQuantityUsed()
                    : otPiece.getQuantityPlanned();
            PieceRechange piece = otPiece.getPieceRechange();
            piece.setStockQty(piece.getStockQty().add(quantiteEffective));
            pieceRechangeRepository.save(piece);
        }

        // Libérer le véhicule si en maintenance
        if (ordre.getEntityType() == OrdreTravail.TypeEntite.VEHICLE) {
            Vehicule vehicule = findVehiculeById(ordre.getEntityId());
            if (vehicule.getStatut() == StatutVehicule.EN_MAINTENANCE) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculeRepository.save(vehicule);
            }
        }

        return mapper.toResponse(ordreTravailRepository.save(ordre),
                resolveEntityRef(ordre.getEntityType(), ordre.getEntityId()));
    }
    @Override
    public OrdreTravailResponse cloturer(Long id) {
        OrdreTravail ordre = findEntityById(id);
        ordre.cloturer(); // les coûts sont maintenant toujours à jour via les getters dynamiques

        if (ordre.getEntityType() == OrdreTravail.TypeEntite.VEHICLE) {
            Vehicule vehicule = findVehiculeById(ordre.getEntityId());
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
            log.info("Véhicule {} remis AVAILABLE après clôture OT {}",
                    vehicule.getReference(), ordre.getReference());
        }

        return mapper.toResponse(ordreTravailRepository.save(ordre),
                resolveEntityRef(ordre.getEntityType(), ordre.getEntityId()));
    }
    @Override
    public OrdreTravailResponse addPiece(Long id, OTPieceRechangeRequest request) {
        OrdreTravail ordre = findEntityById(id);
        if (ordre.getStatut() == OrdreTravail.StatutOT.COMPLETED
                || ordre.getStatut() == OrdreTravail.StatutOT.CANCELLED) {
            throw new InvalidOperationException("Impossible d'ajouter une pièce à un OT clôturé/annulé");
        }

        PieceRechange piece = pieceRechangeRepository.findById(request.pieceRechangeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pièce introuvable avec l'ID = " + request.pieceRechangeId()));

        // Quantité réellement décomptée du stock : l'usage réel si connu, sinon le prévisionnel
        BigDecimal quantiteEffective = request.quantityUsed() != null
                ? request.quantityUsed()
                : request.quantityPlanned();

        if (piece.getStockQty().compareTo(quantiteEffective) < 0) {
            throw new InvalidOperationException(
                    "Stock insuffisant pour \"" + piece.getName() + "\" (disponible: " + piece.getStockQty() + ")");
        }

        piece.setStockQty(piece.getStockQty().subtract(quantiteEffective));
        pieceRechangeRepository.save(piece);

        OTPieceRechange otPiece = new OTPieceRechange();
        otPiece.setOrdreTravail(ordre);
        otPiece.setPieceRechange(piece);
        otPiece.setQuantityPlanned(request.quantityPlanned());
        otPiece.setQuantityUsed(request.quantityUsed());
        otPiece.setUnitCost(piece.getUnitCost());

        ordre.getPieces().add(otPiece);
        OrdreTravail saved = ordreTravailRepository.save(ordre);

        return mapper.toResponse(saved, resolveEntityRef(saved.getEntityType(), saved.getEntityId()));
    }
    @Override
    public OrdreTravailResponse removePiece(Long id, Long pieceId) {
        OrdreTravail ordre = findEntityById(id);

        OTPieceRechange otPiece = ordre.getPieces().stream()
                .filter(p -> p.getId().equals(pieceId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pièce introuvable sur cet OT avec l'ID = " + pieceId));

        BigDecimal quantiteEffective = otPiece.getQuantityUsed() != null
                ? otPiece.getQuantityUsed()
                : otPiece.getQuantityPlanned();

        PieceRechange piece = otPiece.getPieceRechange();
        piece.setStockQty(piece.getStockQty().add(quantiteEffective));
        pieceRechangeRepository.save(piece);

        ordre.getPieces().remove(otPiece);
        OrdreTravail saved = ordreTravailRepository.save(ordre);

        return mapper.toResponse(saved, resolveEntityRef(saved.getEntityType(), saved.getEntityId()));
    }
    @Override
    public OrdreTravailResponse addMainOeuvre(Long id, OTMainOeuvreRequest request) {
        OrdreTravail ordre = findEntityById(id);
        if (ordre.getStatut() == OrdreTravail.StatutOT.COMPLETED
                || ordre.getStatut() == OrdreTravail.StatutOT.CANCELLED) {
            throw new InvalidOperationException(
                    "Impossible d'ajouter de la main d'œuvre à un OT clôturé/annulé");
        }

        OTMainOeuvre mo = new OTMainOeuvre();
        mo.setOrdreTravail(ordre);
        mo.setTechnicianName(request.technicianName());
        mo.setIsExternal(request.isExternal() != null ? request.isExternal() : false);
        mo.setHoursPlanned(request.hoursPlanned());
        mo.setHoursActual(request.hoursActual());
        mo.setHourlyRate(request.hourlyRate());

        ordre.getMainOeuvres().add(mo);
        OrdreTravail saved = ordreTravailRepository.save(ordre);

        return mapper.toResponse(saved, resolveEntityRef(saved.getEntityType(), saved.getEntityId()));
    }

    @Override
    public OrdreTravailResponse removeMainOeuvre(Long id, Long mainOeuvreId) {
        OrdreTravail ordre = findEntityById(id);
        boolean removed = ordre.getMainOeuvres().removeIf(mo -> mo.getId().equals(mainOeuvreId));
        if (!removed) {
            throw new EntityNotFoundException("Main d'œuvre introuvable sur cet OT avec l'ID = " + mainOeuvreId);
        }
        OrdreTravail saved = ordreTravailRepository.save(ordre);
        return mapper.toResponse(saved, resolveEntityRef(saved.getEntityType(), saved.getEntityId()));
    }


    @Override
    public List<StatsSyageResponse> getHistoriqueLamesMachine(Long machineId, Integer idEntreprise) {
        return List.of();
    }
    // ── Méthodes internes ─────────────────────────────────────

    private OrdreTravail findEntityById(Long id) {
        return ordreTravailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ordre de travail introuvable avec l'ID = " + id));
    }

    private Vehicule findVehiculeById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Véhicule introuvable avec l'ID = " + id));
    }

    private String resolveEntityRef(OrdreTravail.TypeEntite type, Long entityId) {
        if (type == OrdreTravail.TypeEntite.VEHICLE) {
            return vehiculeRepository.findById(entityId)
                    .map(v -> v.getReference() + " — " + v.getMarque() + " " + v.getModele())
                    .orElse("Véhicule #" + entityId);
        } else {
            return machineRepository.findById(entityId)
                    .map(m -> m.getReference() + " — " + m.getNom())
                    .orElse("Machine #" + entityId);
        }
    }

    private String generateReference() {
        long count = ordreTravailRepository.count() + 1;
        return String.format("OT-%d-%04d", LocalDate.now().getYear(), count);
    }

    // ── Stats Syage ────────────────────────────────────────────────────────────





    @Override
    @Transactional(readOnly = true)
    public Page<OrdreTravailResponse> findAll(Pageable pageable, OrdreTravail.StatutOT statut,
                                              OrdreTravail.TypeEntite entityType, String search,
                                              LocalDate dateDebut, LocalDate dateFin) {
        String searchParam = (search != null && !search.isBlank())
                ? "%" + search.trim().toLowerCase() + "%"
                : null;

        // dateDebut = début de journée, dateFin = fin de journée (23:59:59.999...)
        // pour rendre le filtre inclusif sur la journée sélectionnée
        LocalDateTime debut = dateDebut != null ? dateDebut.atStartOfDay() : null;
        LocalDateTime fin = dateFin != null ? dateFin.atTime(LocalTime.MAX) : null;

        return ordreTravailRepository
                .findAllFiltered(
                        statut != null ? statut.name() : null,
                        entityType != null ? entityType.name() : null,
                        searchParam, debut, fin, pageable)
                .map(o -> mapper.toResponse(o, resolveEntityRef(o.getEntityType(), o.getEntityId())));

    }
        /**
     * Construit un StatsSyageResponse pour un OT de changement de lames donné.
     * Calcule la somme des hauteurs de tous les blocs syés depuis dateDepuis.
     */
    private Instant getInstantFromOT(OrdreTravail ot) {
        if (ot.getCompletedAt() != null) {
            return ot.getCompletedAt().atZone(ZoneId.systemDefault()).toInstant();
        }
        if (ot.getUpdatedAt() != null) {
            return ot.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant();
        }
        if (ot.getCreatedAt() != null) {
            return ot.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant();
        }
        return Instant.now();
    }
}