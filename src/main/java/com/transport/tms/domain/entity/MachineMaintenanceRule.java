package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "machine_maintenance_rule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MachineMaintenanceRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "machine_id", nullable = false) private Machine machine;
    private String code;
    private String description;
    @Column(name = "action_type") private String actionType;
    @Column(name = "interval_hours") private Integer intervalHours;
    @Column(name = "interval_days") private Integer intervalDays;
    private String consumable;
    private BigDecimal quantity;
    @Column(name = "quantity_unit") private String quantityUnit;
    @Column(name = "last_performed_hours") private BigDecimal lastPerformedHours;
    @Column(name = "last_performed_date") private LocalDate lastPerformedDate;
    @Builder.Default private boolean active = true;
    @Builder.Default @Column(name = "created_at", updatable = false) private Instant createdAt = Instant.now();
}
