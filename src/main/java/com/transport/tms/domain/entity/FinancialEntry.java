package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "financial_entry")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FinancialEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "entry_date") private LocalDate entryDate;
    @Column(name = "entry_type") private String entryType;
    private String category;
    private BigDecimal amount;
    private String description;

    // --- Champs TVA ajoutés ---
    @Column(name = "amount_ht", precision = 15, scale = 2)
    private BigDecimal amountHT;

    @Column(name = "tva_rate", precision = 5, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "tva_amount", precision = 15, scale = 2)
    private BigDecimal tvaAmount;

    @Column(name = "is_tva_recoverable")
    private Boolean isTvaRecoverable = false;

    @Column(name = "is_tva_collected")
    private Boolean isTvaCollected = false;

    @Column(name = "recoverable_tva_amount", precision = 15, scale = 2)
    private BigDecimal recoverableTvaAmount;
    // --------------------------

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
