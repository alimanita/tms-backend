package com.transport.tms.domain.enums;

public enum TypeDocument {
    INSURANCE,          // Assurance
    REGISTRATION,       // Carte grise
    TECHNICAL_CONTROL,  // Visite technique
    PERMIT,             // Autorisation / permis
    CONTRACT,           // Contrat
    PAYSLIP,            // Fiche de paie
    OTHER;

    // Jours d'alerte par défaut avant expiration
    public int getAlertDays() {
        return switch (this) {
            case INSURANCE         -> 30;
            case TECHNICAL_CONTROL -> 30;
            case PERMIT            -> 60;
            default                -> 30;
        };
    }
}