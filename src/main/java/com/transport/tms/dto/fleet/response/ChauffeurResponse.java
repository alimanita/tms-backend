package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.enums.StatutChauffeur;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChauffeurResponse(
        Long id,

        String nom,
        String prenom,
        String cin,
        String telephone,
        String email,
        String adresse,
        LocalDate dateEmbauche,
        
        StatutChauffeur statut,

        String numeroPermis,
        String categoriesPermis,
        LocalDate dateDelivrancePermis,
        LocalDate dateExpirationPermis,
        LocalDate dateExpirationVisiteMedicale,

        BigDecimal totalKilometres,
        Integer nombreIncidents,

        String notes,
        Boolean actif,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long idUtilisateur,
        String utilisateurEmail,
        com.transport.tms.domain.enums.TypeSalaire typeSalaire,
        BigDecimal valeurSalaire,

        // Paramètres de visibilité
        Boolean showTarif,
        Boolean showCout,
        Boolean showCarburant
) {}
