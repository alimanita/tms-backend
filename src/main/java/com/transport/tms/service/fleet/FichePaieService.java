package com.transport.tms.service.fleet;

import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.FichePaie;
import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.FichePaieRepository;
import com.transport.tms.repository.fleet.MissionRepository;
import com.transport.tms.repository.fleet.DocumentFlotteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FichePaieService {

    private final FichePaieRepository fichePaieRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final MissionRepository missionRepository;
    private final FileStorageService fileStorageService;
    private final DocumentFlotteRepository documentFlotteRepository;

    @Transactional
    public FichePaie uploadManual(Long chauffeurId, String moisAnnee, MultipartFile file) {
        Chauffeur chauffeur = chauffeurRepository.findById(chauffeurId)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur non trouvé"));

        Optional<FichePaie> existing = fichePaieRepository.findByChauffeurIdAndMoisAnnee(chauffeurId, moisAnnee);
        FichePaie fichePaie = existing.orElse(new FichePaie());
        
        fichePaie.setChauffeur(chauffeur);
        fichePaie.setMoisAnnee(moisAnnee);
        if (fichePaie.getMontantCalcule() == null) {
            fichePaie.setMontantCalcule(BigDecimal.ZERO);
        }

        if (fichePaie.getUrlDocument() != null) {
            fileStorageService.delete(fichePaie.getUrlDocument(), "payslips");
        }
        
        String filename = fileStorageService.store(file, "payslips");
        fichePaie.setUrlDocument(filename);
        FichePaie saved = fichePaieRepository.save(fichePaie);

        DocumentFlotte doc = new DocumentFlotte();
        doc.setEntityType(DocumentFlotte.TypeEntite.DRIVER);
        doc.setEntityId(chauffeur.getId());
        doc.setTypeDocument(com.transport.tms.domain.enums.TypeDocument.OTHER);
        doc.setReferenceNumber("Fiche de paie - " + moisAnnee);
        doc.setIssuer("Entreprise");
        doc.setIssueDate(java.time.LocalDate.now());
        doc.setFilePath(filename);
        doc.setFileName(file.getOriginalFilename());
        doc.setStatus(DocumentFlotte.StatutDocument.ACTIVE);
        documentFlotteRepository.save(doc);

        return saved;
    }

    @Transactional
    public FichePaie calculerEtGenerer(Long chauffeurId, String moisAnnee) {
        Chauffeur chauffeur = chauffeurRepository.findById(chauffeurId)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur non trouvé"));

        if (chauffeur.getTypeSalaire() == null) {
            throw new IllegalStateException("Le type de salaire du chauffeur n'est pas configuré");
        }

        BigDecimal salaireCalcule = BigDecimal.ZERO;
        
        // Parse moisAnnee (e.g. "2023-10")
        String[] parts = moisAnnee.split("-");
        int annee = Integer.parseInt(parts[0]);
        int mois = Integer.parseInt(parts[1]);
        
        LocalDateTime debutMois = LocalDateTime.of(annee, mois, 1, 0, 0);
        LocalDateTime finMois = debutMois.plusMonths(1).minusSeconds(1);

        switch (chauffeur.getTypeSalaire()) {
            case MENSUEL:
                salaireCalcule = chauffeur.getValeurSalaire() != null ? chauffeur.getValeurSalaire() : BigDecimal.ZERO;
                break;
            case POURCENTAGE:
                List<Mission> missionsPerc = missionRepository.findByChauffeurIdOrderByPlannedDepartureDesc(chauffeurId);
                BigDecimal totalRevenue = missionsPerc.stream()
                        .filter(m -> m.getStatut() == Mission.StatutMission.COMPLETED && m.getActualReturn() != null 
                                && m.getActualReturn().isAfter(debutMois) && m.getActualReturn().isBefore(finMois))
                        .map(m -> m.getRevenue() != null ? m.getRevenue() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                BigDecimal pourcentage = chauffeur.getValeurSalaire() != null ? chauffeur.getValeurSalaire() : BigDecimal.ZERO;
                salaireCalcule = totalRevenue.multiply(pourcentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                break;
            case PARTAGE:
                List<Mission> missionsPartage = missionRepository.findByChauffeurIdOrderByPlannedDepartureDesc(chauffeurId);
                BigDecimal partRevenue = BigDecimal.ZERO;
                BigDecimal partFuel = BigDecimal.ZERO;
                BigDecimal partToll = BigDecimal.ZERO;
                
                for (Mission m : missionsPartage) {
                    if (m.getStatut() == Mission.StatutMission.COMPLETED && m.getActualReturn() != null 
                            && m.getActualReturn().isAfter(debutMois) && m.getActualReturn().isBefore(finMois)) {
                        partRevenue = partRevenue.add(m.getRevenue() != null ? m.getRevenue() : BigDecimal.ZERO);
                        partFuel = partFuel.add(m.getFuelCost() != null ? m.getFuelCost() : BigDecimal.ZERO);
                        partToll = partToll.add(m.getTollCost() != null ? m.getTollCost() : BigDecimal.ZERO);
                    }
                }
                
                BigDecimal reste = partRevenue.subtract(partFuel).subtract(partToll);
                if (reste.compareTo(BigDecimal.ZERO) > 0) {
                    salaireCalcule = reste.divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
                }
                break;
        }

        Optional<FichePaie> existing = fichePaieRepository.findByChauffeurIdAndMoisAnnee(chauffeurId, moisAnnee);
        FichePaie fichePaie = existing.orElse(new FichePaie());
        
        fichePaie.setChauffeur(chauffeur);
        fichePaie.setMoisAnnee(moisAnnee);
        fichePaie.setMontantCalcule(salaireCalcule);
        
        return fichePaieRepository.save(fichePaie);
    }

    @Transactional
    public FichePaie uploadDocument(Long fichePaieId, MultipartFile file) {
        FichePaie fichePaie = fichePaieRepository.findById(fichePaieId)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie non trouvée"));
                
        if (fichePaie.getUrlDocument() != null) {
            fileStorageService.delete(fichePaie.getUrlDocument(), "payslips");
        }
        
        String filename = fileStorageService.store(file, "payslips");
        fichePaie.setUrlDocument(filename);
        return fichePaieRepository.save(fichePaie);
    }
    
    public List<FichePaie> getFichesPaieByChauffeur(Long chauffeurId) {
        return fichePaieRepository.findByChauffeurIdOrderByMoisAnneeDesc(chauffeurId);
    }
}
