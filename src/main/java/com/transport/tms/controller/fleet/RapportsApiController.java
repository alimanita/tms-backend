package com.transport.tms.controller.fleet;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.rapport.*;
import com.transport.tms.service.fleet.RapportEntretiensService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * API controller exposing the same endpoints as {@link RapportEntretiensController} but under the
 * /api/v1/fleet/rapports base path expected by the front‑end.
 */
@RestController
@RequestMapping("/api/v1/fleet/rapports")
@RequiredArgsConstructor
public class RapportsApiController {

    private final RapportEntretiensService rapportService;
    private final com.transport.tms.repository.fleet.MissionRepository missionRepository;
    private final com.transport.tms.repository.FinancialEntryRepository financialEntryRepository;
    private final com.transport.tms.repository.fleet.DepenseMissionRepository depenseMissionRepository;
    private final com.transport.tms.repository.fleet.PleinCarburantRepository pleinCarburantRepository;
    private final com.transport.tms.repository.fleet.OrdreTravailRepository ordreTravailRepository;
    private final com.transport.tms.repository.fleet.ChauffeurRepository chauffeurRepository;

    // ── Entretiens / Maintenance ──────────────────────────────────────────────

    @GetMapping("/entretiens/mensuel")
    public ResponseEntity<List<MaintenanceMensuelleDto>> getEntretiensMensuel(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getRapportMensuel(entityType, d, f));
    }

    @GetMapping("/entretiens/annuel")
    public ResponseEntity<List<MaintenanceAnnuelleDto>> getEntretiensAnnuel(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(defaultValue = "0") int anDebut,
            @RequestParam(defaultValue = "0") int anFin) {
        int currentYear = LocalDate.now().getYear();
        int ad = anDebut > 0 ? anDebut : currentYear - 4;
        int af = anFin > 0 ? anFin : currentYear;
        return ResponseEntity.ok(rapportService.getRapportAnnuel(entityType, ad, af));
    }

    @GetMapping("/entretiens/detail")
    public ResponseEntity<List<MaintenanceDetailDto>> getEntretiensDetail(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getRapportDetail(entityType, d, f));
    }

    @GetMapping("/entretiens/synthese")
    public ResponseEntity<SyntheseEntretiensDto> getEntretiensSynthese(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getSynthese(entityType, d, f));
    }

    // ── Carburant ─────────────────────────────────────────────────────────────

    @GetMapping("/carburant/mensuel")
    public ResponseEntity<List<CarburantMensuelDto>> getCarburantMensuel(
            @RequestParam(required = false) Long vehiculeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getCarburantMensuel(vehiculeId, d, f));
    }

    @GetMapping("/carburant/annuel")
    public ResponseEntity<List<CarburantAnnuelDto>> getCarburantAnnuel(
            @RequestParam(required = false) Long vehiculeId,
            @RequestParam(defaultValue = "0") int anDebut,
            @RequestParam(defaultValue = "0") int anFin) {
        int currentYear = LocalDate.now().getYear();
        int ad = anDebut > 0 ? anDebut : currentYear - 4;
        int af = anFin > 0 ? anFin : currentYear;
        return ResponseEntity.ok(rapportService.getCarburantAnnuel(vehiculeId, ad, af));
    }

    // --- Nouveaux Rapports (Missions, Amazon, Finance) ---

    @GetMapping("/missions/stats")
    public ResponseEntity<MissionStatsDto> getMissionsStats() {
        MissionStatsDto dto = new MissionStatsDto();
        dto.setMissionsByDriver(convertToLongMap(missionRepository.countMissionsByDriver()));
        dto.setMissionsByStatus(convertToLongMap(missionRepository.countMissionsByStatus()));
        dto.setMileageByDriver(convertToBigDecimalMap(missionRepository.sumMileageByDriver()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/finance/stats")
    public ResponseEntity<FinanceStatsDto> getFinanceStats() {
        FinanceStatsDto dto = new FinanceStatsDto();
        dto.setMonthlyRevenue(convertToBigDecimalMap(missionRepository.sumRevenueByMonth()));
        
        java.util.Map<String, java.math.BigDecimal> combinedExpenses = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> generalMonth = convertToBigDecimalMap(financialEntryRepository.sumExpensesByMonth());
        
        generalMonth.forEach((k, v) -> combinedExpenses.merge(k, v, java.math.BigDecimal::add));
        
        dto.setMonthlyExpenses(combinedExpenses);
        
        java.util.Map<String, java.math.BigDecimal> results = new java.util.HashMap<>();
        dto.getMonthlyRevenue().forEach((month, rev) -> {
            java.math.BigDecimal exp = combinedExpenses.getOrDefault(month, java.math.BigDecimal.ZERO);
            results.put(month, rev.subtract(exp));
        });
        // Pour les mois avec dépenses mais sans revenus
        combinedExpenses.forEach((month, exp) -> {
            if (!results.containsKey(month)) {
                results.put(month, java.math.BigDecimal.ZERO.subtract(exp));
            }
        });
        
        dto.setMonthlyResult(results);
        dto.setFleetExpensesByCategory(convertToBigDecimalMap(financialEntryRepository.sumExpensesByCategory()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/amazon/stats")
    public ResponseEntity<com.transport.tms.dto.fleet.rapport.AmazonStatsDto> getAmazonStats(
            @RequestParam(defaultValue = "mensuel") String mode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "0") int anDebut,
            @RequestParam(defaultValue = "0") int anFin) {

        com.transport.tms.dto.fleet.rapport.AmazonStatsDto dto = new com.transport.tms.dto.fleet.rapport.AmazonStatsDto();
        java.util.Map<String, java.math.BigDecimal> expensesByMonth = new java.util.TreeMap<>();

        if ("annuel".equals(mode)) {
            int ad = anDebut > 0 ? anDebut : LocalDate.now().getYear() - 4;
            int af = anFin   > 0 ? anFin   : LocalDate.now().getYear();

            // OT par année
            for (Object[] row : ordreTravailRepository.sumCostByYearMonth(
                    java.time.LocalDateTime.now().minusYears(10))) {
                // On ne garde que les données annuelles agrégées plus bas
            }

            // Agrégation annuelle : Dépenses mission + Carburant + OT
            java.util.Map<String, java.math.BigDecimal> annual = new java.util.TreeMap<>();
            for (Object[] row : depenseMissionRepository.sumCostByYearMonth(
                    java.time.LocalDateTime.of(ad, 1, 1, 0, 0))) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    String key = row[0].toString();
                    annual.merge(key, new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
            for (Object[] row : pleinCarburantRepository.sumCostByYearMonth(
                    java.time.LocalDateTime.of(ad, 1, 1, 0, 0))) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    String key = row[0].toString();
                    annual.merge(key, new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
            for (Object[] row : ordreTravailRepository.sumCostByYearMonth(
                    java.time.LocalDateTime.of(ad, 1, 1, 0, 0))) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    String key = row[0].toString();
                    annual.merge(key, new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
            expensesByMonth = annual;
        } else {
            // Mode mensuel
            java.time.LocalDateTime d = debut != null
                    ? debut.atStartOfDay()
                    : LocalDate.now().withDayOfYear(1).atStartOfDay();
            java.time.LocalDateTime f = fin != null
                    ? fin.atTime(23, 59, 59)
                    : LocalDate.now().atTime(23, 59, 59);

            for (Object[] row : depenseMissionRepository.sumCostByYearMonth(d)) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    expensesByMonth.merge(row[1].toString(),
                            new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
            for (Object[] row : pleinCarburantRepository.sumCostByYearMonth(d)) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    expensesByMonth.merge(row[1].toString(),
                            new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
            for (Object[] row : ordreTravailRepository.sumCostByYearMonth(d)) {
                if (row[0] != null && row[1] != null && row[2] != null) {
                    expensesByMonth.merge(row[1].toString(),
                            new java.math.BigDecimal(row[2].toString()), java.math.BigDecimal::add);
                }
            }
        }

        // Catégories (toutes périodes)
        java.util.Map<String, java.math.BigDecimal> expensesBySupplier = new java.util.LinkedHashMap<>();
        for (Object[] row : depenseMissionRepository.sumCostByExpenseType()) {
            if (row[0] != null && row[1] != null)
                expensesBySupplier.merge(row[0].toString(),
                        new java.math.BigDecimal(row[1].toString()), java.math.BigDecimal::add);
        }
        java.math.BigDecimal tc = pleinCarburantRepository.sumAllCoutCarburant();
        if (tc != null && tc.compareTo(java.math.BigDecimal.ZERO) > 0)
            expensesBySupplier.merge("CARBURANT", tc, java.math.BigDecimal::add);
        java.math.BigDecimal tm = ordreTravailRepository.sumAllCout();
        if (tm != null && tm.compareTo(java.math.BigDecimal.ZERO) > 0)
            expensesBySupplier.merge("MAINTENANCE", tm, java.math.BigDecimal::add);

        dto.setExpensesByMonth(expensesByMonth);
        dto.setExpensesBySupplier(expensesBySupplier);
        return ResponseEntity.ok(dto);
    }

    // ── RAPPORT CHAUFFEUR ──────────────────────────────────────────────────────

    @GetMapping("/chauffeur/liste")
    public ResponseEntity<List<java.util.Map<String, Object>>> getListeChauffeurs() {
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        chauffeurRepository.findAll().forEach(c -> {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", c.getId());
            m.put("nom", c.getPrenom() + " " + c.getNom());
            m.put("typeSalaire", c.getTypeSalaire());
            m.put("valeurSalaire", c.getValeurSalaire());
            result.add(m);
        });
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chauffeur/stats")
    public ResponseEntity<com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto> getChauffeurStats(
            @RequestParam(required = false) Long chauffeurId,
            @RequestParam(defaultValue = "month") String periodMode,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto dto =
                new com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto();
        dto.setChauffeurId(chauffeurId);

        if (year == null || year == 0) year = LocalDate.now().getYear();
        if (month == null || month == 0) month = LocalDate.now().getMonthValue();

        java.time.LocalDateTime debut, fin;

        if ("month".equals(periodMode)) {
            debut = LocalDate.of(year, month, 1).atStartOfDay();
            fin = LocalDate.of(year, month, debut.toLocalDate().lengthOfMonth()).atTime(23, 59, 59);
        } else if ("year".equals(periodMode)) {
            debut = LocalDate.of(year, 1, 1).atStartOfDay();
            fin = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
        } else {
            // all
            debut = LocalDate.of(2000, 1, 1).atStartOfDay();
            fin = LocalDate.now().plusYears(1).atTime(23, 59, 59);
        }

        // Infos chauffeur
        if (chauffeurId != null) {
            chauffeurRepository.findById(chauffeurId).ifPresent(c -> {
                dto.setChauffeurNom(c.getPrenom() + " " + c.getNom());
                dto.setValeurSalaire(c.getValeurSalaire());
                dto.setTypeSalaire(c.getTypeSalaire() != null ? c.getTypeSalaire().name() : "MENSUEL");
            });
        } else {
            dto.setChauffeurNom("Tous les chauffeurs");
        }

        List<com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow> details = new java.util.ArrayList<>();

        if (chauffeurId == null) {
            // Initialiser la map avec TOUS les chauffeurs
            java.util.Map<Long, com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow> map = new java.util.HashMap<>();
            chauffeurRepository.findAll().forEach(c -> {
                com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow r = new com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow();
                r.setChauffeurId(c.getId());
                r.setNom(c.getPrenom() + " " + c.getNom());
                
                java.math.BigDecimal valSalaire = c.getValeurSalaire();
                String typeSalaire = c.getTypeSalaire() != null ? c.getTypeSalaire().name() : "MENSUEL";
                
                // Calcul du salaire fixe s'il est MENSUEL (même sans mission)
                java.math.BigDecimal salaire = java.math.BigDecimal.ZERO;
                if (valSalaire != null && "MENSUEL".equals(typeSalaire)) {
                    salaire = "month".equals(periodMode) ? valSalaire : ("year".equals(periodMode) ? valSalaire.multiply(new java.math.BigDecimal("12")) : valSalaire);
                }
                r.setSalaire(salaire);
                map.put(c.getId(), r);
            });

            // Mettre à jour avec les stats de missions
            for (Object[] row : missionRepository.statsTousChauffeursSurPeriode(debut, fin)) {
                if (row[0] == null) continue;
                Long cId = ((Number) row[0]).longValue();
                com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow r = map.get(cId);
                if (r == null) continue;

                r.setRevenu(new java.math.BigDecimal(row[5].toString()));
                r.setDepense(new java.math.BigDecimal(row[6].toString()));
                r.setNbMissions(((Number) row[7]).longValue());

                // Recalcul du salaire si c'est au pourcentage
                java.math.BigDecimal valSalaire = row[3] != null ? new java.math.BigDecimal(row[3].toString()) : null;
                String typeSalaire = row[4] != null ? row[4].toString() : "MENSUEL";
                if (valSalaire != null && ("POURCENTAGE".equals(typeSalaire) || "PARTAGE".equals(typeSalaire))) {
                    java.math.BigDecimal base = r.getRevenu().subtract(r.getDepense());
                    if (base.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        r.setSalaire(base.multiply(valSalaire).divide(new java.math.BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP));
                    }
                }
            }

            // Calculer les totaux et le bénéfice
            for (com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow r : map.values()) {
                r.setBenefice(r.getRevenu().subtract(r.getDepense()).subtract(r.getSalaire()));
                dto.setTotalRevenu(dto.getTotalRevenu().add(r.getRevenu()));
                dto.setTotalDepense(dto.getTotalDepense().add(r.getDepense()));
                dto.setTotalSalaire(dto.getTotalSalaire().add(r.getSalaire()));
                dto.setTotalMissions(dto.getTotalMissions() + r.getNbMissions());
                details.add(r);
            }
            // Trier par nom
            details.sort((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()));
        } else {
            // Détails des missions pour un chauffeur
            // D'abord, on calcule le salaire fixe de la période si MENSUEL
            java.math.BigDecimal salaireFixePeriode = java.math.BigDecimal.ZERO;
            // null typeSalaire est traité comme MENSUEL
            String effectiveType = dto.getTypeSalaire() != null ? dto.getTypeSalaire() : "MENSUEL";
            if (("MENSUEL".equals(effectiveType)) && dto.getValeurSalaire() != null) {
                salaireFixePeriode = "month".equals(periodMode) ? dto.getValeurSalaire() : ("year".equals(periodMode) ? dto.getValeurSalaire().multiply(new java.math.BigDecimal("12")) : dto.getValeurSalaire());
                dto.setTotalSalaire(salaireFixePeriode);
            }

            for (Object[] row : missionRepository.missionsChauffeurSurPeriode(chauffeurId, debut, fin)) {
                if (row[0] == null) continue;
                com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow r = new com.transport.tms.dto.fleet.rapport.ChauffeurStatsDto.DetailRow();
                r.setReference(row[1] != null ? row[1].toString() : "Mission #" + row[0]);
                r.setDate(row[2] != null ? row[2].toString().substring(0, 10) : "");
                r.setRevenu(row[3] != null ? new java.math.BigDecimal(row[3].toString()) : java.math.BigDecimal.ZERO);
                r.setDepense(row[4] != null ? new java.math.BigDecimal(row[4].toString()) : java.math.BigDecimal.ZERO);
                r.setNbMissions(1L);

                // Calcul du salaire (uniquement si pourcentage par mission)
                java.math.BigDecimal salaireMission = java.math.BigDecimal.ZERO;
                if (dto.getValeurSalaire() != null && ("POURCENTAGE".equals(dto.getTypeSalaire()) || "PARTAGE".equals(dto.getTypeSalaire()))) {
                    java.math.BigDecimal base = r.getRevenu().subtract(r.getDepense());
                    if (base.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        salaireMission = base.multiply(dto.getValeurSalaire()).divide(new java.math.BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                    }
                }
                r.setSalaire(salaireMission);
                r.setBenefice(r.getRevenu().subtract(r.getDepense()).subtract(salaireMission));

                dto.setTotalRevenu(dto.getTotalRevenu().add(r.getRevenu()));
                dto.setTotalDepense(dto.getTotalDepense().add(r.getDepense()));
                if (!("MENSUEL".equals(effectiveType))) {
                    dto.setTotalSalaire(dto.getTotalSalaire().add(r.getSalaire()));
                }
                dto.setTotalMissions(dto.getTotalMissions() + 1);
                details.add(r);
            }
        }

        dto.setTotalBenefice(dto.getTotalRevenu().subtract(dto.getTotalDepense()).subtract(dto.getTotalSalaire()));
        dto.setDetails(details);
        return ResponseEntity.ok(dto);
    }

    
    private java.util.Map<String, Long> convertToLongMap(List<Object[]> queryResult) {
        java.util.Map<String, Long> map = new java.util.HashMap<>();
        for (Object[] row : queryResult) {
            if (row[0] != null && row[1] != null) {
                map.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
            }
        }
        return map;
    }
    
    private java.util.Map<String, java.math.BigDecimal> convertToBigDecimalMap(List<Object[]> queryResult) {
        java.util.Map<String, java.math.BigDecimal> map = new java.util.HashMap<>();
        for (Object[] row : queryResult) {
            if (row[0] != null && row[1] != null) {
                map.put(String.valueOf(row[0]), new java.math.BigDecimal(row[1].toString()));
            }
        }
        return map;
    }
}
