package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;


@Entity
@Table(name = "fuel_filling")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class PleinCarburant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Chauffeur chauffeur;

    @Column(name = "filling_date", nullable = false)
    private LocalDateTime fillingDate = LocalDateTime.now();

    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType;

    @Column(name = "quantity_liters", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityLiters;

    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 3)
    private BigDecimal pricePerLiter;

    @Column(name = "mileage_before", precision = 12, scale = 2)
    private BigDecimal mileageBefore;

    @Column(name = "mileage_after", precision = 12, scale = 2)
    private BigDecimal mileageAfter;

    @Column(name = "distance_since_last", precision = 12, scale = 2)
    private BigDecimal distanceSinceLast;

    @Column(name = "consumption_rate", precision = 8, scale = 3)
    private BigDecimal consumptionRate; // L/100km — calculé

    @Column(name = "is_full_tank")
    private Boolean isFullTank = true;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "amount_ht", precision = 12, scale = 3)
    private BigDecimal amountHT;

    @Column(name = "amount_ttc", precision = 12, scale = 3)
    private BigDecimal amountTTC;

    @Column(name = "tva_rate", precision = 5, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "tva_amount", precision = 12, scale = 3)
    private BigDecimal tvaAmount;

    @Column(name = "is_tva_recoverable")
    private Boolean isTvaRecoverable = false;

    @Column(name = "recoverable_tva_amount", precision = 12, scale = 3)
    private BigDecimal recoverableTvaAmount;

    @Column(name = "accise_amount", precision = 12, scale = 3)
    private BigDecimal acciseAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(name = "proof_file_path", length = 255)
    private String proofFilePath;
    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public BigDecimal getTotalAmount() {
        if (amountTTC != null) return amountTTC;
        if (quantityLiters == null || pricePerLiter == null) return BigDecimal.ZERO;
        return quantityLiters.multiply(pricePerLiter);
    }

    public void calculerConsommation() {
        if (mileageAfter != null && mileageBefore != null
                && mileageAfter.compareTo(mileageBefore) > 0) {
            this.distanceSinceLast = mileageAfter.subtract(mileageBefore);
            this.consumptionRate = quantityLiters
                    .multiply(BigDecimal.valueOf(100))
                    .divide(distanceSinceLast, 3, RoundingMode.HALF_UP);
        }
    }
}