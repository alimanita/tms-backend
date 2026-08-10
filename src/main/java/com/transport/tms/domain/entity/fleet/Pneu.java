package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "tire")
@Getter @Setter @NoArgsConstructor
public class Pneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", unique = true, length = 100)
    private String serialNumber;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(length = 50)
    private String size; // ex: 225/65R17

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private TypePneu type;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_cost", precision = 15, scale = 2)
    private BigDecimal purchaseCost;

    @Column(name = "max_km", precision = 12, scale = 2)
    private BigDecimal maxKm;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private StatutPneu status = StatutPneu.STOCK;

    @Column(name = "is_active")
    private Boolean isActive = true;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TypePneu { SUMMER, WINTER, ALL_SEASON }
    public enum StatutPneu { STOCK, MOUNTED, RETREADING, SCRAP }
}