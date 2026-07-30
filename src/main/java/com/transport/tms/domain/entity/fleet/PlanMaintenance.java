package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.TypeMaintenance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "maintenance_plan")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class PlanMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntite entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;


    @JoinColumn(name = "maintenance_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMaintenance typeMaintenance;

    @Column(name = "trigger_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeDeclencheur triggerType; // KM | HOURS | CALENDAR

    @Column(name = "trigger_value", precision = 12, scale = 2)
    private BigDecimal triggerValue;

    @Column(name = "trigger_unit", length = 20)
    private String triggerUnit; // KM | HOURS | DAYS | MONTHS

    @Column(name = "last_performed_date")
    private LocalDate lastPerformedDate;

    @Column(name = "last_performed_km", precision = 12, scale = 2)
    private BigDecimal lastPerformedKm;

    @Column(name = "last_performed_hours", precision = 12, scale = 2)
    private BigDecimal lastPerformedHours;

    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    @Column(name = "next_due_km", precision = 12, scale = 2)
    private BigDecimal nextDueKm;

    @Column(name = "next_due_hours", precision = 12, scale = 2)
    private BigDecimal nextDueHours;

    @Column(name = "alert_threshold", precision = 10, scale = 2)
    private BigDecimal alertThreshold;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TypeEntite { VEHICLE, MACHINE }
    public enum TypeDeclencheur { KM, HOURS, CALENDAR }
}