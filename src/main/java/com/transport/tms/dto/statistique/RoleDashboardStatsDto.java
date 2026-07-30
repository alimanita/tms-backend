package com.transport.tms.dto.statistique;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO unique retourné par /statistics/role-dashboard
 * Chaque rôle utilise les champs qui le concernent.
 * Les champs non pertinents pour un rôle restent à 0 / null.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDashboardStatsDto {

    // ── Commun ────────────────────────────────────────────────────────────────
    /** Identifiant de l'entreprise */
    private Integer idEntreprise;

    // ── VENDEUR ───────────────────────────────────────────────────────────────
    /** Nombre de factures créées aujourd'hui */
    private long    facturesAujourdhui;

    /** Nombre de BL en attente (BROUILLON / EN_COURS) */
    private long    blEnAttente;

    /** Nombre de factures impayées */
    private long    facturesImpayees;

    // ── COMPTABLE ─────────────────────────────────────────────────────────────
    /** Nombre de factures dont l'échéance tombe ce mois-ci */
    private long    echeancesMois;

    /** Nombre de paiements en attente (EtatPaiement.EN_ATTENTE) */
    private long    paiementsEnAttente;

    /** Montant total encaissé ce mois (paiements PAYEE du mois courant) */
    private BigDecimal encaisseMois;

    // ── MANAGER ───────────────────────────────────────────────────────────────
    /** Nombre de commandes traitées (état LIVREE ou FACTUREE) */
    private long    commandesTraitees;

    /** Nombre de commandes en cours (état EN_COURS / VALIDEE) */
    private long    commandesEnCours;

    // ── MAGASINIER ────────────────────────────────────────────────────────────
    /** Nombre de BL à traiter aujourd'hui */
    private long    blDuJour;

    /** Nombre d'articles en stock critique (quantité ≤ seuilAlerte) */
    private long    stockCritique;

    // ── CHAUFFEUR ─────────────────────────────────────────────────────────────
    /** Nombre de véhicules assignés au chauffeur connecté (généralement 0 ou 1) */
    private long    mesVehicules;
    /** Missions actuellement en cours pour ce chauffeur */
    private long    missionsEnCours;
    /** Maintenances programmées à venir sur son/ses véhicule(s) */
    private long    maintenancesAVenir;

    // ── MECANICIEN ────────────────────────────────────────────────────────────
    /** Nombre de machines dont ce mécanicien est responsable */
    private long    mesMachines;
    /** Machines actuellement en panne parmi les siennes */
    private long    machinesEnPanne;
    /** Ordres de travail en cours assignés à ce mécanicien */
    private long    ordresTravailEnCours;
    /** Ordres de travail terminés (ex: ce mois-ci) */
    private long    ordresTravailTermines;
    /** Machines opérationnelles parmi les siennes */
    private long    machinesOperationnelles;
    /** Pièces de rechange sous seuil minimum (stock critique) */
    private long    piecesCritiquesMeca;
    /** Règles de maintenance actives sur les machines du mécanicien */
    private long    reglesMaintenanceActives;
    /** OT planifiés pour ce mois sur les machines du mécanicien */
    private long    otPlanifiesMois;
    /** Coût total de maintenance ce mois (OT terminés) */
    private BigDecimal coutMaintenanceMois;

    // ── CHAUFFEUR (étendu) ────────────────────────────────────────────────────
    /** Véhicules disponibles assignés au chauffeur */
    private long    vehiculesDisponibles;
    /** Missions terminées ce mois */
    private long    missionsTerminees;
    /** Nombre de pleins carburant effectués ce mois */
    private long    pleinsCeMois;
    /** Coût total carburant ce mois */
    private BigDecimal coutCarburantMois;
}