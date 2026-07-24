package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.TirePosition;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "tire_assignment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TireAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tire_id", nullable = false)
    private Tire tire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TirePosition position;

    @Column(name = "mount_date", nullable = false)
    private LocalDate mountDate;

    @Column(name = "mount_mileage", nullable = false)
    private BigDecimal mountMileage;

    @Column(name = "unmount_date")
    private LocalDate unmountDate;

    @Column(name = "unmount_mileage")
    private BigDecimal unmountMileage;

    @Column(name = "reason_unmount")
    private String reasonUnmount;

    private String notes;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
