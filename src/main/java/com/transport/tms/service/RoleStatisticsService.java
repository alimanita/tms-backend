package com.transport.tms.service;

import com.transport.tms.dto.statistique.RoleDashboardStatsDto;

;

public interface RoleStatisticsService {

    /**
     * @param idEntreprise filtre entreprise
     * @param username identifiant (login) de l'utilisateur connecté,
     *                  utilisé pour filtrer les stats CHAUFFEUR/MECANICIEN
     *                  sur leurs propres véhicules/machines.
     */
    RoleDashboardStatsDto computeStats(Integer idEntreprise, String username);
}