package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "oil_change")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OilChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "oil_type", nullable = false)
    private String oilType;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "mileage_at_change", nullable = false)
    private BigDecimal mileageAtChange;

    @Column(name = "quantity_liters", nullable = false)
    private BigDecimal quantityLiters;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "next_change_km")
    private BigDecimal nextChangeKm;

    @Column(name = "next_change_date")
    private LocalDate nextChangeDate;

    @Column(name = "performed_by")
    private String performedBy;

    private String notes;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
