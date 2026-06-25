package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "amazon_purchase")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AmazonPurchase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "amazon_order_number", nullable = false, unique = true) private String amazonOrderNumber;
    @Column(name = "purchase_date", nullable = false) private LocalDate purchaseDate;
    private String supplier;
    @Column(name = "amount_ht") private BigDecimal amountHt;
    @Column(name = "vat_amount") private BigDecimal vatAmount;
    @Column(name = "amount_ttc") private BigDecimal amountTtc;
    @Column(name = "shipping_cost") private BigDecimal shippingCost;
    private String currency;
    private String status;
    private String notes;
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<AmazonPurchaseItem> items = new java.util.ArrayList<>();
}
