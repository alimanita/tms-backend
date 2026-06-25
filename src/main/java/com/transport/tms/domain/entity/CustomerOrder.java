package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.CustomerOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "customer_order")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String reference;
    @Column(name = "order_date", nullable = false) private LocalDate orderDate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private Customer customer;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CustomerOrderStatus status;
    @Column(name = "total_amount") private BigDecimal totalAmount;
    private String notes;
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<CustomerOrderLine> lines = new ArrayList<>();
}
