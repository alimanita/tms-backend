package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record SocietePartenaireRequest(
    @NotBlank(message = "Le nom est obligatoire")
    String nom,
    String matriculeFiscal,
    String adresse,
    String contact,
    String telephone,
    String email,
    String iban,
    String statut,
    BigDecimal tauxCommissionDefaut
) {}
