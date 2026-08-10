package com.transport.tms.service.fleet;

import com.transport.tms.domain.entity.FinancialEntry;
import com.transport.tms.domain.entity.fleet.*;
import com.transport.tms.dto.fleet.response.GlobalTvaReportDto;
import com.transport.tms.repository.FinancialEntryRepository;
import com.transport.tms.repository.fleet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GlobalTvaService {

    private final PleinCarburantRepository pleinCarburantRepository;
    private final OrdreTravailRepository ordreTravailRepository;
    private final DepenseMissionRepository depenseMissionRepository;
    private final PneuRepository pneuRepository;
    private final PieceRechangeRepository pieceRechangeRepository;
    private final FinancialEntryRepository financialEntryRepository;

    public GlobalTvaReportDto getGlobalReport(String periodMode, Integer year, Integer month) {
        // --- 1. TVA Déductible ---
        List<GlobalTvaReportDto.TvaCategoryDto> deductibles = new ArrayList<>();
        List<Map<String, Object>> details = new ArrayList<>();

        BigDecimal totalDedHT = BigDecimal.ZERO;
        BigDecimal totalDedTTC = BigDecimal.ZERO;
        BigDecimal totalDedTva = BigDecimal.ZERO;

        // Helper to filter dates
        java.util.function.Predicate<java.time.LocalDateTime> isDateInRange = date -> {
            if (date == null) return false;
            if ("year".equals(periodMode) && year != null) {
                return date.getYear() == year;
            } else if ("month".equals(periodMode) && year != null && month != null) {
                return date.getYear() == year && date.getMonthValue() == month;
            }
            return true; // "all"
        };
        java.util.function.Predicate<java.time.LocalDate> isLocalDateInRange = date -> {
            if (date == null) return false;
            if ("year".equals(periodMode) && year != null) {
                return date.getYear() == year;
            } else if ("month".equals(periodMode) && year != null && month != null) {
                return date.getYear() == year && date.getMonthValue() == month;
            }
            return true;
        };

        // Carburant
        BigDecimal carbHT = BigDecimal.ZERO, carbTTC = BigDecimal.ZERO, carbTva = BigDecimal.ZERO;
        BigDecimal totalLiters = BigDecimal.ZERO, totalAccise = BigDecimal.ZERO;
        for (PleinCarburant p : pleinCarburantRepository.findAll()) {
            if (!isDateInRange.test(p.getFillingDate())) continue;

            BigDecimal ttc = p.getAmountTTC() != null ? p.getAmountTTC() : p.getTotalAmount();
            BigDecimal tvaRate = p.getTvaRate() != null ? p.getTvaRate() : BigDecimal.valueOf(20);
            BigDecimal ht = p.getAmountHT() != null ? p.getAmountHT() : ttc.divide(BigDecimal.ONE.add(tvaRate.divide(BigDecimal.valueOf(100))), 3, java.math.RoundingMode.HALF_UP);
            BigDecimal tva = p.getTvaAmount() != null ? p.getTvaAmount() : ttc.subtract(ht);
            Boolean isTvaRecoverable = p.getIsTvaRecoverable() != null ? p.getIsTvaRecoverable() : true;
            BigDecimal recTva = p.getRecoverableTvaAmount() != null ? p.getRecoverableTvaAmount() : (isTvaRecoverable ? tva : BigDecimal.ZERO);

            carbHT = carbHT.add(ht);
            carbTTC = carbTTC.add(ttc);
            carbTva = carbTva.add(recTva);
            
            totalLiters = totalLiters.add(p.getQuantityLiters() != null ? p.getQuantityLiters() : BigDecimal.ZERO);
            totalAccise = totalAccise.add(p.getAcciseAmount() != null ? p.getAcciseAmount() : BigDecimal.ZERO);

            details.add(createDetailMap("Carburant", p.getFillingDate(), p.getReference(), ht, ttc, recTva));
        }
        deductibles.add(new GlobalTvaReportDto.TvaCategoryDto("Carburant", carbHT, carbTTC, carbTva));

        // Péages (DepenseMission where type = TOLL)
        BigDecimal peageHT = BigDecimal.ZERO, peageTTC = BigDecimal.ZERO, peageTva = BigDecimal.ZERO;
        for (DepenseMission d : depenseMissionRepository.findAll()) {
            if (!isDateInRange.test(d.getExpenseDate())) continue;

            if (d.getExpenseType() == DepenseMission.TypeDepense.TOLL) {
                BigDecimal ttc = d.getMontant();
                BigDecimal tvaRate = d.getTvaRate() != null ? d.getTvaRate() : BigDecimal.valueOf(20);
                BigDecimal ht = d.getAmountHT() != null ? d.getAmountHT() : ttc.divide(BigDecimal.ONE.add(tvaRate.divide(BigDecimal.valueOf(100))), 3, java.math.RoundingMode.HALF_UP);
                BigDecimal tva = d.getTvaAmount() != null ? d.getTvaAmount() : ttc.subtract(ht);
                Boolean isTvaRecoverable = d.getIsTvaRecoverable() != null ? d.getIsTvaRecoverable() : true;
                BigDecimal recTva = d.getRecoverableTvaAmount() != null ? d.getRecoverableTvaAmount() : (isTvaRecoverable ? tva : BigDecimal.ZERO);

                peageHT = peageHT.add(ht);
                peageTTC = peageTTC.add(ttc);
                peageTva = peageTva.add(recTva);
                
                details.add(createDetailMap("Péages", d.getExpenseDate(), "Mission " + d.getMission().getId(), ht, ttc, recTva));
            }
        }
        deductibles.add(new GlobalTvaReportDto.TvaCategoryDto("Péages", peageHT, peageTTC, peageTva));

        // Maintenance (OrdreTravail)
        BigDecimal maintHT = BigDecimal.ZERO, maintTTC = BigDecimal.ZERO, maintTva = BigDecimal.ZERO;
        for (OrdreTravail o : ordreTravailRepository.findAll()) {
             if (!isDateInRange.test(o.getCompletedAt())) continue;

             BigDecimal ttc = o.getActualTotalCost() != null ? o.getActualTotalCost() : BigDecimal.ZERO;
             if (ttc.compareTo(BigDecimal.ZERO) > 0) {
                 BigDecimal tvaRate = o.getTvaRate() != null ? o.getTvaRate() : BigDecimal.valueOf(20);
                 BigDecimal ht = o.getAmountHT() != null ? o.getAmountHT() : ttc.divide(BigDecimal.ONE.add(tvaRate.divide(BigDecimal.valueOf(100))), 3, java.math.RoundingMode.HALF_UP);
                 BigDecimal tva = o.getTvaAmount() != null ? o.getTvaAmount() : ttc.subtract(ht);
                 Boolean isTvaRecoverable = o.getIsTvaRecoverable() != null ? o.getIsTvaRecoverable() : true;
                 BigDecimal recTva = o.getRecoverableTvaAmount() != null ? o.getRecoverableTvaAmount() : (isTvaRecoverable ? tva : BigDecimal.ZERO);

                 maintHT = maintHT.add(ht);
                 maintTTC = maintTTC.add(ttc);
                 maintTva = maintTva.add(recTva);
                 
                 details.add(createDetailMap("Maintenance", o.getCompletedAt(), o.getReference(), ht, ttc, recTva));
             }
        }
        deductibles.add(new GlobalTvaReportDto.TvaCategoryDto("Maintenance", maintHT, maintTTC, maintTva));

        // FinancialEntry (Achats divers, etc.)
        BigDecimal diversHT = BigDecimal.ZERO, diversTTC = BigDecimal.ZERO, diversTva = BigDecimal.ZERO;
        BigDecimal collHT = BigDecimal.ZERO, collTTC = BigDecimal.ZERO, collTva = BigDecimal.ZERO;
        List<GlobalTvaReportDto.TvaCategoryDto> collectees = new ArrayList<>();
        List<Map<String, Object>> collecteeDetails = new ArrayList<>();

        for (FinancialEntry f : financialEntryRepository.findAll()) {
            if (!isLocalDateInRange.test(f.getEntryDate())) continue;
            BigDecimal ttc = f.getAmount();
            BigDecimal tvaRate = f.getTvaRate() != null ? f.getTvaRate() : BigDecimal.valueOf(20);
            BigDecimal ht = f.getAmountHT() != null ? f.getAmountHT() : ttc.divide(BigDecimal.ONE.add(tvaRate.divide(BigDecimal.valueOf(100))), 3, java.math.RoundingMode.HALF_UP);
            BigDecimal tva = f.getTvaAmount() != null ? f.getTvaAmount() : ttc.subtract(ht);
            
            if ("REVENUE".equals(f.getEntryType())) {
                collHT = collHT.add(ht);
                collTTC = collTTC.add(ttc);
                collTva = collTva.add(tva); // La TVA collectée est due en totalité
                collecteeDetails.add(createDetailMap(f.getCategory(), f.getEntryDate(), f.getDescription(), ht, ttc, tva));
            } else {
                Boolean isTvaRecoverable = f.getIsTvaRecoverable() != null ? f.getIsTvaRecoverable() : true;
                BigDecimal recTva = f.getRecoverableTvaAmount() != null ? f.getRecoverableTvaAmount() : (isTvaRecoverable ? tva : BigDecimal.ZERO);
                diversHT = diversHT.add(ht);
                diversTTC = diversTTC.add(ttc);
                diversTva = diversTva.add(recTva);
                details.add(createDetailMap(f.getCategory() != null ? f.getCategory() : "Divers", f.getEntryDate(), f.getDescription(), ht, ttc, recTva));
            }
        }
        deductibles.add(new GlobalTvaReportDto.TvaCategoryDto("Autres Dépenses", diversHT, diversTTC, diversTva));
        collectees.add(new GlobalTvaReportDto.TvaCategoryDto("Prestations de Transport & Ventes", collHT, collTTC, collTva));

        for (GlobalTvaReportDto.TvaCategoryDto cat : deductibles) {
            totalDedHT = totalDedHT.add(cat.getAmountHT());
            totalDedTTC = totalDedTTC.add(cat.getAmountTTC());
            totalDedTva = totalDedTva.add(cat.getTvaAmount());
        }

        GlobalTvaReportDto.TvaSectionDto secDed = new GlobalTvaReportDto.TvaSectionDto(totalDedHT, totalDedTTC, totalDedTva, deductibles, details);
        GlobalTvaReportDto.TvaSectionDto secCol = new GlobalTvaReportDto.TvaSectionDto(collHT, collTTC, collTva, collectees, collecteeDetails);

        BigDecimal netAmount = collTva.subtract(totalDedTva);
        String status = netAmount.compareTo(BigDecimal.ZERO) > 0 ? "A_PAYER" : "CREDIT_TVA";
        GlobalTvaReportDto.NetTvaDto net = new GlobalTvaReportDto.NetTvaDto(collTva, totalDedTva, netAmount.abs(), status);

        // Approximation du remboursement d'accise
        BigDecimal acciseReimbursement = totalLiters.multiply(BigDecimal.valueOf(0.15)); // Exemple arbitraire
        GlobalTvaReportDto.FiscaliteCarburantDto fiscalite = new GlobalTvaReportDto.FiscaliteCarburantDto(totalLiters, totalAccise, acciseReimbursement);

        return new GlobalTvaReportDto(secCol, secDed, net, fiscalite);
    }

    private Map<String, Object> createDetailMap(String category, Object date, String reference, BigDecimal ht, BigDecimal ttc, BigDecimal tva) {
        Map<String, Object> map = new HashMap<>();
        map.put("category", category);
        map.put("date", date);
        map.put("reference", reference);
        map.put("amountHT", ht);
        map.put("amountTTC", ttc);
        map.put("tvaAmount", tva);
        return map;
    }
}
