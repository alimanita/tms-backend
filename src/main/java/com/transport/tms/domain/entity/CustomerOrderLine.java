package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "customer_order_line")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerOrderLine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_order_id") private CustomerOrder customerOrder;
    @Column(name = "product_ref") private String productRef;
    @Column(nullable = false) private String designation;
    private BigDecimal quantity;
    @Column(name = "sale_price") private BigDecimal salePrice;
    @Column(name = "total_price") private BigDecimal totalPrice;
}
