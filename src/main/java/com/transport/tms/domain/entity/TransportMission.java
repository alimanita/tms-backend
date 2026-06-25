package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.MissionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "transport_mission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportMission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String reference;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_order_id") private CustomerOrder customerOrder;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id") private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vehicle_id") private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "driver_id") private Driver driver;
    @Column(name = "departure_date") private Instant departureDate;
    @Column(name = "expected_arrival") private Instant expectedArrival;
    @Column(name = "actual_arrival") private Instant actualArrival;
    @Column(name = "loading_address") private String loadingAddress;
    @Column(name = "delivery_address") private String deliveryAddress;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MissionStatus status;
    private BigDecimal revenue;
    @Column(name = "transport_cost") private BigDecimal transportCost;
    private String notes;
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
