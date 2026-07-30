package com.transport.tms.controller.api;

import com.transport.tms.dto.statistique.RoleDashboardStatsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.transport.tms.utils.Constants.APP_ROOT;


@Tag(name = "Role Statistics", description = "Statistiques dashboard par rôle (Vendeur, Comptable, Manager, Magasinier)")
public interface RoleStatisticsApi {

    @GetMapping(
            value = APP_ROOT + "/statistics/role-dashboard",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Statistiques dashboard par rôle",
            description = "Retourne les KPIs adaptés au rôle de l'utilisateur connecté : " +
                    "VENDEUR (factures du jour, BL en attente, impayées), " +
                    "COMPTABLE (impayées, échéances, paiements en attente, encaissé mois), " +
                    "MANAGER (commandes traitées, en cours), " +
                    "MAGASINIER (BL du jour, stock critique)"
    )
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    @ApiResponse(responseCode = "403", description = "Accès refusé")
    RoleDashboardStatsDto getRoleDashboardStats(
            @RequestParam(required = false) Integer idEntreprise
    );
}