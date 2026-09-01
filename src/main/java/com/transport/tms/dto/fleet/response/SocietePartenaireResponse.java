package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;

public record SocietePartenaireResponse(
    Long id,
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
