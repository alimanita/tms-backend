package com.transport.tms.dto.response;

public record ChauffeurResponse(
        Long id,
        String nom,
        String prenom,
        String cin,
        String telephone,
        String email,
        String adresse,
        String dateEmbauche,
        String statut,
        String numeroPermis,
        String categoriesPermis,
        String dateDelivrancePermis,
        String dateExpirationPermis,
        String dateExpirationVisiteMedicale,
        Double totalKilometres,
        Integer nombreIncidents,
        String notes,
        boolean actif,
        String createdAt,
        String updatedAt,
        Long idUtilisateur,
        String utilisateurEmail
) {}
