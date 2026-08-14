package com.transport.tms.dto.fleet.rapport;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ChauffeurStatsDto {

    private Long chauffeurId;
    private String chauffeurNom;
    private BigDecimal salaire;
    private String typeSalaire;
    private BigDecimal valeurSalaire;

    /** Totaux globaux sur la période */
    private BigDecimal totalRevenu      = BigDecimal.ZERO;
    private BigDecimal totalDepense     = BigDecimal.ZERO;
    private BigDecimal totalSalaire     = BigDecimal.ZERO;
    private BigDecimal totalBenefice    = BigDecimal.ZERO;
    private Long       totalMissions    = 0L;

    /** Lignes par mois ou par année */
    @Data
    public static class DetailRow {
        // Utilisé quand chauffeurId est null (liste de chauffeurs)
        private Long   chauffeurId;
        private String nom;
        
        // Utilisé quand chauffeurId n'est pas null (liste de missions/opérations)
        private String date;
        private String reference;
        
        // Champs communs
        private BigDecimal revenu = BigDecimal.ZERO;
        private BigDecimal depense = BigDecimal.ZERO;
        private BigDecimal salaire = BigDecimal.ZERO;
        private BigDecimal benefice = BigDecimal.ZERO;
        private Long nbMissions = 0L;
    }

    private List<DetailRow> details = new java.util.ArrayList<>();
}
