package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardOverviewResponse(
    // Statuts véhicules
    long totalVehicules,
    long vehiculesDisponibles,
    long vehiculesEnMission,
    long vehiculesEnMaintenance,
    long vehiculesHorsService,

    // Coûts du mois courant
    BigDecimal coutCarburantMois,
    BigDecimal coutMaintenanceMois,
    BigDecimal coutTotalMois,
    BigDecimal coutMoyenParKm,

    // Missions
    long missionsEnCours,
    long missionsEnAttente,   // PLANNED non approuvées

    // Alertes
    long alertesCritiques,
    long alertesWarning,

    // Top véhicules coûteux
    List<VehiculeResponse> topVehiculesCoûteux
) {}