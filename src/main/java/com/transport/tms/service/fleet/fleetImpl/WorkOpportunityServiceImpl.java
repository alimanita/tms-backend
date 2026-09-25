package com.transport.tms.service.fleet.fleetImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tms.domain.entity.fleet.WorkOpportunity;
import com.transport.tms.domain.enums.StatutOffre;
import com.transport.tms.dto.fleet.request.WorkOpportunityImportItem;
import com.transport.tms.dto.fleet.request.WorkOpportunityImportRequest;
import com.transport.tms.dto.fleet.response.WorkOpportunityImportResult;
import com.transport.tms.dto.fleet.response.WorkOpportunityResponse;
import com.transport.tms.mapper.fleet.WorkOpportunityMapper;
import com.transport.tms.repository.fleet.WorkOpportunityRepository;
import com.transport.tms.service.fleet.WorkOpportunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOpportunityServiceImpl implements WorkOpportunityService {

    private final WorkOpportunityRepository repository;
    private final WorkOpportunityMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WorkOpportunityImportResult importBatch(WorkOpportunityImportRequest request) {
        int imported = 0;
        int skipped = 0;

        List<WorkOpportunityImportItem> items = request.getWorkOpportunities();
        if (items == null || items.isEmpty()) {
            return new WorkOpportunityImportResult(0, 0, 0);
        }

        int total = items.size();
        String source = request.getSource() != null && !request.getSource().isBlank() ? request.getSource() : "IMPORT";
        java.util.Set<String> seenInBatch = new java.util.HashSet<>();

        for (WorkOpportunityImportItem item : items) {
            try {
                String depCity = item.getStartLocation() != null ? item.getStartLocation().getCity() : null;
                String arrCity = item.getEndLocation() != null ? item.getEndLocation().getCity() : null;
                LocalDateTime pickupTime = parseDate(item.getFirstPickupTime());
                LocalDateTime deliveryTime = parseDate(item.getLastDeliveryTime());
                BigDecimal payoutVal = item.getPayout() != null ? item.getPayout().getValue() : null;

                String externalId = item.getId();
                if (externalId == null || externalId.isBlank()) {
                    // Si aucun ID n'est fourni, générer une clé unique déterministe
                    String c1 = depCity != null ? depCity.trim().toLowerCase() : "";
                    String c2 = arrCity != null ? arrCity.trim().toLowerCase() : "";
                    String t1 = pickupTime != null ? pickupTime.toString() : "";
                    String pVal = payoutVal != null ? payoutVal.toPlainString() : "";
                    externalId = String.format("%s_%s_%s_%s", c1, c2, t1, pVal);
                }

                // 1. Anti-doublon dans le même lot d'import par externalId
                if (!seenInBatch.add(externalId)) {
                    skipped++;
                    continue;
                }

                // 2. Recherche si la course existe déjà par son externalId unique (UPSERT)
                WorkOpportunity entity = repository.findByExternalId(externalId).orElse(null);
                if (entity == null) {
                    entity = new WorkOpportunity();
                    entity.setExternalId(externalId);
                    entity.setStatut(StatutOffre.DISPONIBLE);
                }

                entity.setSource(source);
                entity.setFirstPickupTime(pickupTime);
                entity.setLastDeliveryTime(deliveryTime);

                if (item.getStartLocation() != null) {
                    entity.setDepartureCity(depCity);
                    entity.setDepartureLat(item.getStartLocation().getLatitude());
                    entity.setDepartureLng(item.getStartLocation().getLongitude());
                }

                if (item.getEndLocation() != null) {
                    entity.setArrivalCity(arrCity);
                    entity.setArrivalLat(item.getEndLocation().getLatitude());
                    entity.setArrivalLng(item.getEndLocation().getLongitude());
                }

                if (payoutVal != null) {
                    entity.setPayoutValue(payoutVal);
                    entity.setPayoutUnit(item.getPayout().getUnit() != null ? item.getPayout().getUnit() : "EUR");
                } else if (entity.getPayoutUnit() == null) {
                    entity.setPayoutUnit("EUR");
                }

                if (item.getTotalDistance() != null) {
                    entity.setDistanceKm(item.getTotalDistance().getValue());
                }

                entity.setCargoType(item.getCargoType());
                entity.setCargoWeightKg(item.getCargoWeightKg());
                entity.setCargoVolumeM3(item.getCargoVolumeM3());
                entity.setNotes(item.getNotes());

                try {
                    entity.setRawJson(objectMapper.writeValueAsString(item));
                } catch (Exception e) {
                    log.warn("Failed to serialize rawJson for WorkOpportunity {}", externalId, e);
                }

                repository.save(entity);
                imported++;
            } catch (Exception e) {
                log.error("Failed to import individual work opportunity item", e);
                skipped++;
            }
        }

        return new WorkOpportunityImportResult(imported, skipped, total);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkOpportunityResponse> getFiltered(StatutOffre statut, LocalDateTime startDate, LocalDateTime endDate,
                                                     BigDecimal minRevenue, String departureCity, String arrivalCity,
                                                     Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<WorkOpportunity> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (statut != null) {
                predicates.add(cb.equal(root.get("statut"), statut));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("firstPickupTime"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastDeliveryTime"), endDate));
            }
            if (minRevenue != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("payoutValue"), minRevenue));
            }
            if (departureCity != null && !departureCity.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("departureCity")), "%" + departureCity.trim().toLowerCase() + "%"));
            }
            if (arrivalCity != null && !arrivalCity.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("arrivalCity")), "%" + arrivalCity.trim().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Override
    public List<WorkOpportunityResponse> findNearby(double lat, double lng, double radiusKm, LocalDateTime afterDate,
                                                    BigDecimal minRevenue, int limit) {
        String afterDateStr = afterDate != null ? afterDate.toString() : null;
        List<Object[]> rawResults = repository.findNearbyDepartures(lat, lng, radiusKm, afterDateStr, minRevenue, limit);

        return rawResults.stream()
                .map(row -> {
                    try {
                        Long entityId = ((Number) row[0]).longValue();
                        Double emptyKm = row.length > 1 && row[1] != null
                                ? ((Number) row[1]).doubleValue()
                                : 0.0;

                        return repository.findById(entityId)
                                .map(entity -> mapper.toResponse(entity, emptyKm))
                                .orElse(null);
                    } catch (Exception e) {
                        log.error("Error mapping native query result row to WorkOpportunityResponse", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkOpportunityResponse updateStatut(Long id, StatutOffre statut) {
        WorkOpportunity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkOpportunity not found with id " + id));
        entity.setStatut(statut);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public int purgeExpired() {
        return repository.deleteExpired(LocalDateTime.now());
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(dateStr).toLocalDateTime();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateStr);
            } catch (DateTimeParseException ex) {
                log.warn("Could not parse date string: {}", dateStr);
                return null;
            }
        }
    }
}
