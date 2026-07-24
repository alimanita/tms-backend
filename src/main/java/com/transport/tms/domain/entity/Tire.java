package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.TireStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tire")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    private String brand;
    private String model;
    private String size;
    
    @Column(name = "tire_type")
    private String type;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_cost")
    private BigDecimal purchaseCost;

    @Column(name = "max_km")
    private BigDecimal maxKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TireStatus status;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
