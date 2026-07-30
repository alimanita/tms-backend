package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.StatutChauffeur;
import jakarta.validation.constraints.NotBlank;


import java.math.BigDecimal;
import java.time.LocalDate;

public record ChauffeurRequest(


        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
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
          Long idUtilisateur
) {}
