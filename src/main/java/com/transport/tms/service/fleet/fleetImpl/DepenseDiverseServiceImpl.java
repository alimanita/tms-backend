package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.DepenseDiverse;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.CategorieDepense;
import com.transport.tms.dto.fleet.request.DepenseDiverseRequest;
import com.transport.tms.dto.fleet.response.DepenseDiverseResponse;
import com.transport.tms.dto.fleet.response.DepenseDiverseSummaryResponse;
import com.transport.tms.mapper.fleet.DepenseDiverseMapper;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.DepenseDiverseRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.service.fleet.DepenseDiverseService;
import com.transport.tms.service.fleet.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepenseDiverseServiceImpl implements DepenseDiverseService {

    private final DepenseDiverseRepository depenseRepository;
    private final DepenseDiverseMapper depenseMapper;
    private final ChauffeurRepository chauffeurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final FileStorageService fileStorageService;
    private final UtilisateurRepository utilisateurRepository;

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    // ── Résolution du chauffeur connecté ────────────────────────────────

    private Chauffeur resolveChauffeurFromConnectedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        boolean isChauffeur = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CHAUFFEUR"));
        if (!isChauffeur) return null;
        String username = auth.getName();
        return utilisateurRepository.findByEmailOrUsername(username, username)
                .flatMap(u -> chauffeurRepository.findByUtilisateurId(u.getId()))
                .orElse(null);
    }

    // ── findAll ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<DepenseDiverseResponse> findAll(
            Long chauffeurId,
            String categorie,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        Chauffeur chauffeurConnecte = resolveChauffeurFromConnectedUser();
        Long finalChauffeurId = (chauffeurConnecte != null) ? chauffeurConnecte.getId() : chauffeurId;

        Specification<DepenseDiverse> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (finalChauffeurId != null) {
                predicates.add(cb.equal(root.get("chauffeur").get("id"), finalChauffeurId));
            }
            if (categorie != null && !categorie.isBlank()) {
                try {
                    CategorieDepense cat = CategorieDepense.valueOf(categorie);
                    predicates.add(cb.equal(root.get("categorie"), cat));
                } catch (IllegalArgumentException ignored) {}
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateDepense"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateDepense"), endDate));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return depenseRepository.findAll(spec, pageable).map(depenseMapper::toResponse);
    }

    // ── getSummary ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DepenseDiverseSummaryResponse getSummary(
            Long chauffeurId,
            String categorie,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        Chauffeur chauffeurConnecte = resolveChauffeurFromConnectedUser();
        Long finalChauffeurId = (chauffeurConnecte != null) ? chauffeurConnecte.getId() : chauffeurId;

        jakarta.persistence.criteria.CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        jakarta.persistence.criteria.CriteriaQuery<jakarta.persistence.Tuple> cq = cb.createTupleQuery();
        jakarta.persistence.criteria.Root<DepenseDiverse> root = cq.from(DepenseDiverse.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
        if (finalChauffeurId != null) {
            predicates.add(cb.equal(root.get("chauffeur").get("id"), finalChauffeurId));
        }
        if (categorie != null && !categorie.isBlank()) {
            try {
                CategorieDepense cat = CategorieDepense.valueOf(categorie);
                predicates.add(cb.equal(root.get("categorie"), cat));
            } catch (IllegalArgumentException ignored) {}
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dateDepense"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dateDepense"), endDate));
        }

        cq.multiselect(
            cb.coalesce(cb.sum(root.get("amountTTC")), BigDecimal.ZERO).alias("totalAmountTTC"),
            cb.count(root).alias("totalCount")
        );
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }

        jakarta.persistence.Tuple tuple = entityManager.createQuery(cq).getSingleResult();
        BigDecimal totalTTC = tuple.get("totalAmountTTC", BigDecimal.class);
        Long count = tuple.get("totalCount", Long.class);

        return DepenseDiverseSummaryResponse.builder()
                .totalAmountTTC(totalTTC != null ? totalTTC : BigDecimal.ZERO)
                .totalCount(count != null ? count : 0L)
                .build();
    }

    // ── findById ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DepenseDiverseResponse findById(Long id) {
        return depenseMapper.toResponse(findEntityById(id));
    }

    // ── create ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DepenseDiverseResponse create(DepenseDiverseRequest request, MultipartFile proof) {
        Chauffeur chauffeur = chauffeurRepository.findById(request.chauffeurId())
                .orElseThrow(() -> new EntityNotFoundException("Chauffeur non trouvé"));

        DepenseDiverse depense = depenseMapper.toEntity(request);
        depense.setReference(genererReference());
        depense.setChauffeur(chauffeur);

        if (request.vehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                    .orElseThrow(() -> new EntityNotFoundException("Véhicule non trouvé"));
            depense.setVehicule(vehicule);
        }

        if (proof != null && !proof.isEmpty()) {
            String filename = fileStorageService.store(proof, "");
            depense.setProofFilePath(filename);
        }

        depense = depenseRepository.save(depense);
        return depenseMapper.toResponse(depense);
    }

    // ── update ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DepenseDiverseResponse update(Long id, DepenseDiverseRequest request, MultipartFile proof) {
        DepenseDiverse depense = findEntityById(id);

        Chauffeur chauffeur = chauffeurRepository.findById(request.chauffeurId())
                .orElseThrow(() -> new EntityNotFoundException("Chauffeur non trouvé"));

        depense.setChauffeur(chauffeur);
        depense.setDateDepense(request.dateDepense());
        depense.setCategorie(request.categorie());
        depense.setDescription(request.description());
        depense.setAmountTTC(request.amountTTC());
        depense.setReceiptNumber(request.receiptNumber());
        depense.setNotes(request.notes());

        if (request.vehiculeId() != null) {
            Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                    .orElseThrow(() -> new EntityNotFoundException("Véhicule non trouvé"));
            depense.setVehicule(vehicule);
        } else {
            depense.setVehicule(null);
        }

        if (proof != null && !proof.isEmpty()) {
            if (depense.getProofFilePath() != null) {
                fileStorageService.delete(depense.getProofFilePath(), "");
            }
            String filename = fileStorageService.store(proof, "");
            depense.setProofFilePath(filename);
        }

        depense = depenseRepository.save(depense);
        return depenseMapper.toResponse(depense);
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void delete(Long id) {
        DepenseDiverse depense = findEntityById(id);
        if (depense.getProofFilePath() != null) {
            fileStorageService.delete(depense.getProofFilePath(), "");
        }
        depenseRepository.delete(depense);
    }

    // ── findByChauffeur ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<DepenseDiverseResponse> findByChauffeur(Long chauffeurId) {
        return depenseRepository.findByChauffeurId(chauffeurId).stream()
                .map(depenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ── getProofFile ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Resource getProofFile(Long id) {
        DepenseDiverse depense = findEntityById(id);
        if (depense.getProofFilePath() == null) {
            throw new EntityNotFoundException("Aucun justificatif pour cette dépense");
        }
        return fileStorageService.load(depense.getProofFilePath());
    }

    // ── helpers ──────────────────────────────────────────────────────────

    private DepenseDiverse findEntityById(Long id) {
        return depenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dépense diverse non trouvée : " + id));
    }

    private String genererReference() {
        return "DD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
