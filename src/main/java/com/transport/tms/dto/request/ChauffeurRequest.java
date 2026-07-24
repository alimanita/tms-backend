package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ChauffeurRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        String telephone,
        String email,
        String numeroPermis,
        LocalDate dateExpirationPermis,
        String statut,
        Boolean actif,
        Long idUtilisateur,
        Long idEntreprise
) {}
