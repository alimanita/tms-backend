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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ordre_travail")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class OrdreTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference; // MO-2024-0001

    @Column(name = "entity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntite entityType; // VEHICLE | MACHINE

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @JoinColumn(name = "maintenance_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMaintenance typeMaintenance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private PlanMaintenance planMaintenance;

    @Column(name = "order_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeOrdre typeOrdre; // PREVENTIVE | CORRECTIVE

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private PrioriteOT priorite = PrioriteOT.NORMAL;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private StatutOT statut = StatutOT.PLANNED;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "reported_by")
    private Long reportedBy;

    @Column(name = "reported_date")
    private LocalDate reportedDate;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "mileage_at_order", precision = 12, scale = 2)
    private BigDecimal mileageAtOrder;

    @Column(name = "hours_at_order", precision = 12, scale = 2)
    private BigDecimal hoursAtOrder;

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(length = 200)
    private String workshop;

    @Column(name = "is_external")
    private Boolean isExternal = false;

    @Column(name = "external_provider", length = 200)
    private String externalProvider;

    @Column(name = "estimated_cost", precision = 15, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "actual_labor_cost", precision = 15, scale = 2)
    private BigDecimal actualLaborCost;

    @Column(name = "actual_parts_cost", precision = 15, scale = 2)
    private BigDecimal actualPartsCost;

    @Column(name = "downtime_hours", precision = 10, scale = 2)
    private BigDecimal downtimeHours;

    // --- Champs TVA ajoutés ---
    @Column(name = "amount_ht", precision = 15, scale = 2)
    private BigDecimal amountHT;

    @Column(name = "tva_rate", precision = 5, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "tva_amount", precision = 15, scale = 2)
    private BigDecimal tvaAmount;

    @Column(name = "is_tva_recoverable")
    private Boolean isTvaRecoverable = false;

    @Column(name = "recoverable_tva_amount", precision = 15, scale = 2)
    private BigDecimal recoverableTvaAmount;
    // --------------------------

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "ordreTravail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OTPieceRechange> pieces = new ArrayList<>();

    @OneToMany(mappedBy = "ordreTravail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OTMainOeuvre> mainOeuvres = new ArrayList<>();

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Méthodes métier ───────────────────────────────────────
    public BigDecimal getActualPartsCost() {
        return pieces.stream()
                .map(OTPieceRechange::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getActualLaborCost() {
        return mainOeuvres.stream()
                .map(OTMainOeuvre::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getActualTotalCost() {
        return getActualPartsCost().add(getActualLaborCost());
    }



    public void demarrer() {
        if (this.statut != StatutOT.PLANNED && this.statut != StatutOT.PLANNED) {
            throw new IllegalStateException("L'OT doit être en statut PLANNED ou DRAFT pour démarrer");
        }
        this.statut = StatutOT.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void cloturer() {
        if (this.statut != StatutOT.IN_PROGRESS) {
            throw new IllegalStateException("L'OT doit être IN_PROGRESS pour être clôturé");
        }
        this.statut = StatutOT.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void annuler() {
        if (this.statut == StatutOT.COMPLETED) {
            throw new IllegalStateException("Un OT complété ne peut pas être annulé");
        }
        this.statut = StatutOT.CANCELLED;
    }

    // ── Enums internes ────────────────────────────────────────

    public enum TypeEntite { VEHICLE, MACHINE }

    public enum TypeOrdre { PREVENTIVE, CORRECTIVE }

    public enum PrioriteOT { LOW, NORMAL, HIGH, CRITICAL }

    public enum StatutOT { PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED }
}