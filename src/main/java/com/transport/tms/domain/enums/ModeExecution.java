package com.transport.tms.domain.enums;

public enum ModeExecution {
    INTERNAL,
    SUBCONTRACTED,      // Vous confiez à un partenaire → bénéfice = revenue × taux%
    PARTNER_MISSION     // Un partenaire vous confie une mission → bénéfice = revenue × (100% - taux%)
}
