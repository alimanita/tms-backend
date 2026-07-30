package com.transport.tms.domain.entity.fleet;

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
@Table(name = "tire_assignment")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class AffectationPneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tire_id", nullable = false)
    private Pneu pneu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicule vehicule;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PositionPneu position; // FL | FR | RL | RR | SPARE...

    @Column(name = "mount_date", nullable = false)
    private LocalDate mountDate;

    @Column(name = "mount_mileage", nullable = false, precision = 12, scale = 2)
    private BigDecimal mountMileage;

    @Column(name = "unmount_date")
    private LocalDate unmountDate;

    @Column(name = "unmount_mileage", precision = 12, scale = 2)
    private BigDecimal unmountMileage;

    @Column(name = "reason_unmount", length = 100)
    @Enumerated(EnumType.STRING)
    private RaisonDemontage reasonUnmount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BigDecimal getKmUsed() {
        if (unmountMileage == null || mountMileage == null) return BigDecimal.ZERO;
        return unmountMileage.subtract(mountMileage);
    }

    public enum PositionPneu { FL, FR, RL, RR, RL_INT, RR_INT, SPARE }
    public enum RaisonDemontage { ROTATION, WORN, DAMAGED, SEASONAL }
}