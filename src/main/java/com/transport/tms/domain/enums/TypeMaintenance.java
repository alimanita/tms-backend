package com.transport.tms.domain.enums;

public enum TypeMaintenance {

    // ── Préventive ─────────────────────────────────────────────
    VIDANGE("Vidange huile moteur", CategorieMaintenance.PREVENTIVE),
    CHANGEMENT_LAMES("Changement de lames de coupe (syage)", CategorieMaintenance.PREVENTIVE),
    FILTRE_AIR("Remplacement filtre à air", CategorieMaintenance.PREVENTIVE),
    FILTRE_CARBURANT("Remplacement filtre carburant", CategorieMaintenance.PREVENTIVE),
    FILTRE_HUILE("Remplacement filtre à huile", CategorieMaintenance.PREVENTIVE),
    COURROIE_DISTRIBUTION("Remplacement courroie de distribution", CategorieMaintenance.PREVENTIVE),
    BOUGIES("Remplacement bougies", CategorieMaintenance.PREVENTIVE),
    FREINS("Révision freins", CategorieMaintenance.PREVENTIVE),
    PNEUS_ROTATION("Rotation des pneus", CategorieMaintenance.PREVENTIVE),
    REVISION_GENERALE("Révision générale", CategorieMaintenance.PREVENTIVE),
    GRAISSAGE("Graissage / lubrification", CategorieMaintenance.PREVENTIVE),

    // ── Corrective ─────────────────────────────────────────────
    PANNE_MOTEUR("Réparation moteur", CategorieMaintenance.CORRECTIVE),
    PANNE_ELECTRICITE("Réparation électrique", CategorieMaintenance.CORRECTIVE),
    PANNE_FREINAGE("Réparation système de freinage", CategorieMaintenance.CORRECTIVE),
    PANNE_TRANSMISSION("Réparation transmission", CategorieMaintenance.CORRECTIVE),
    PANNE_SUSPENSION("Réparation suspension", CategorieMaintenance.CORRECTIVE),
    ACCIDENT("Réparation suite accident", CategorieMaintenance.CORRECTIVE),
    AUTRE("Autre intervention", CategorieMaintenance.CORRECTIVE);

    private final String label;
    private final CategorieMaintenance categorie;

    TypeMaintenance(String label, CategorieMaintenance categorie) {
        this.label = label;
        this.categorie = categorie;
    }

    public String getLabel() { return label; }
    public CategorieMaintenance getCategorie() { return categorie; }

    public enum CategorieMaintenance { PREVENTIVE, CORRECTIVE }
}