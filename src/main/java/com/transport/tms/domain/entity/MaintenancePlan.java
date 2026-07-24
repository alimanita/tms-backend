package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.MaintenanceTriggerType;
import com.transport.tms.domain.enums.MaintenanceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_plan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false)
    private MaintenanceType maintenanceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private MaintenanceTriggerType triggerType;

    @Column(name = "trigger_value", nullable = false)
    private BigDecimal triggerValue;

    @Column(name = "last_performed_date")
    private LocalDate lastPerformedDate;

    @Column(name = "last_performed_km")
    private BigDecimal lastPerformedKm;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "next_due_km")
    private BigDecimal nextDueKm;

    @Column(name = "alert_threshold")
    private BigDecimal alertThreshold;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
