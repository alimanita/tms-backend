package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "spare_part")
@Getter @Setter @NoArgsConstructor
public class PieceRechange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(length = 20)
    private String unit = "PCS"; // PCS | L | KG | M

    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "stock_qty", precision = 10, scale = 2)
    private BigDecimal stockQty = BigDecimal.ZERO;

    @Column(name = "min_stock_qty", precision = 10, scale = 2)
    private BigDecimal minStockQty = BigDecimal.ZERO;

    @Column(name = "stock_item_id")
    private Long stockItemId; // Lien module stock

    @Column(length = 100)
    private String location;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isLowStock() {
        return stockQty.compareTo(minStockQty) <= 0;
    }
}