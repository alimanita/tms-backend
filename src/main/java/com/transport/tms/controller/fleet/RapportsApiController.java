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
    private final com.transport.tms.repository.AmazonPurchaseRepository amazonPurchaseRepository;
    private final com.transport.tms.repository.FinancialEntryRepository financialEntryRepository;

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

    @GetMapping("/amazon/stats")
    public ResponseEntity<AmazonStatsDto> getAmazonStats() {
        AmazonStatsDto dto = new AmazonStatsDto();
        dto.setExpensesByMonth(convertToBigDecimalMap(amazonPurchaseRepository.sumExpensesByMonth()));
        dto.setExpensesBySupplier(convertToBigDecimalMap(amazonPurchaseRepository.sumExpensesBySupplier()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/finance/stats")
    public ResponseEntity<FinanceStatsDto> getFinanceStats() {
        FinanceStatsDto dto = new FinanceStatsDto();
        dto.setMonthlyRevenue(convertToBigDecimalMap(missionRepository.sumRevenueByMonth()));
        
        java.util.Map<String, java.math.BigDecimal> combinedExpenses = new java.util.HashMap<>();
        java.util.Map<String, java.math.BigDecimal> amazonMonth = convertToBigDecimalMap(amazonPurchaseRepository.sumExpensesByMonth());
        java.util.Map<String, java.math.BigDecimal> generalMonth = convertToBigDecimalMap(financialEntryRepository.sumExpensesByMonth());
        
        amazonMonth.forEach((k, v) -> combinedExpenses.merge(k, v, java.math.BigDecimal::add));
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
