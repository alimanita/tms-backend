package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mission")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference; // MSN-2024-0001

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "client_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Chauffeur chauffeur;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private StatutMission statut = StatutMission.PLANNED;

    @Column(name = "departure_location", nullable = false, length = 300)
    private String departureLocation;

    @Column(name = "arrival_location", nullable = false, length = 300)
    private String arrivalLocation;

    @Column(name = "planned_departure", nullable = false)
    private LocalDateTime plannedDeparture;

    @Column(name = "planned_return")
    private LocalDateTime plannedReturn;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Column(name = "actual_return")
    private LocalDateTime actualReturn;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "cargo_description", columnDefinition = "TEXT")
    private String cargoDescription;

    @Column(name = "cargo_weight", precision = 10, scale = 2)
    private BigDecimal cargoWeight;

    @Column(name = "mileage_at_departure", precision = 12, scale = 2)
    private BigDecimal mileageAtDeparture;

    @Column(name = "mileage_at_return", precision = 12, scale = 2)
    private BigDecimal mileageAtReturn;

    @Column(name = "fuel_cost", precision = 15, scale = 2)
    private BigDecimal fuelCost;

    @Column(name = "toll_cost", precision = 15, scale = 2)
    private BigDecimal tollCost;

    @Column(name = "other_expenses", precision = 15, scale = 2)
    private BigDecimal otherExpenses;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "revenue", precision = 15, scale = 2)
    private BigDecimal revenue;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepenseMission> depenses = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

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

    public BigDecimal getTotalKm() {
        if (mileageAtReturn == null || mileageAtDeparture == null) return BigDecimal.ZERO;
        return mileageAtReturn.subtract(mileageAtDeparture);
    }



    public void demarrer() {
        if (this.statut != StatutMission.PLANNED) {
            throw new IllegalStateException("La mission doit être PLANNED pour démarrer");
        }
        this.statut = StatutMission.IN_PROGRESS;
        this.actualDeparture = LocalDateTime.now();
    }

    public void cloturer() {
        this.statut = StatutMission.COMPLETED;
        this.actualReturn = LocalDateTime.now();
        recalculerCoutTotal();
    }

    public void recalculerCoutTotal() {
        this.totalCost = depenses.stream()
                .map(DepenseMission::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    public enum StatutMission {  PLANNED,  IN_PROGRESS, COMPLETED, CANCELLED }
}