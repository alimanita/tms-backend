package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "amazon_purchase_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmazonPurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private AmazonPurchase purchase;

    private String reference;
    @Column(nullable = false)
    private String designation;
    @Column(nullable = false)
    private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    @Column(name = "weight_kg")
    private BigDecimal weightKg;
    @Column(name = "volume_m3")
    private BigDecimal volumeM3;
}
