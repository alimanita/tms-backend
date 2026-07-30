package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.TypeHuile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "oil_change")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class ChangementHuile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(name = "entity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntite entityType; // VEHICLE | MACHINE

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "oil_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TypeHuile typeHuile;

    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;

    @Column(name = "mileage_at_change", precision = 12, scale = 2)
    private BigDecimal mileageAtChange;

    @Column(name = "hours_at_change", precision = 12, scale = 2)
    private BigDecimal hoursAtChange;

    @Column(name = "quantity_liters", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantityLiters;

    @Column(name = "unit_cost", precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "next_change_km", precision = 12, scale = 2)
    private BigDecimal nextChangeKm;

    @Column(name = "next_change_hours", precision = 12, scale = 2)
    private BigDecimal nextChangeHours;

    @Column(name = "next_change_date")
    private LocalDate nextChangeDate;

    @Column(name = "performed_by", length = 200)
    private String performedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TypeEntite { VEHICLE, MACHINE }
}