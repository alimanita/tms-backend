package com.transport.tms.domain.entity;

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
@Getter
@Setter
@NoArgsConstructor
public class PieceRechange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @Column(name = "designation", nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String supplier;

    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "stock_qty", precision = 10, scale = 2)
    private BigDecimal stockQty = BigDecimal.ZERO;

    @Column(name = "min_threshold", precision = 10, scale = 2)
    private BigDecimal minStockQty = BigDecimal.ZERO;

    @Column(name = "active")
    private Boolean isActive = true;

    public boolean isLowStock() {
        return stockQty.compareTo(minStockQty) <= 0;
    }
}