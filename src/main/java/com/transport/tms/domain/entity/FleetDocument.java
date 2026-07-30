package com.transport.tms.domain.entity;

import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "fleet_document")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FleetDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicule vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Chauffeur driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "reference_number")
    private String referenceNumber;

    private String issuer;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private BigDecimal amount;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    private String status;
    private String notes;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
