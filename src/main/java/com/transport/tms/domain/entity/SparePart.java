package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "spare_part")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SparePart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String reference;
    @Column(nullable = false) private String designation;
    private String category;
    private String supplier;
    @Column(name = "purchase_price") private BigDecimal purchasePrice;
    @Column(name = "stock_qty") private BigDecimal stockQty;
    @Column(name = "min_threshold") private BigDecimal minThreshold;
    @Builder.Default
    private boolean active = true;
}
