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
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
