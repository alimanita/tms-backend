package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "fuel_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FuelRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "vehicle_id") private Vehicle vehicle;
    @ManyToOne @JoinColumn(name = "driver_id") private Driver driver;
    @Column(name = "fill_date") private Instant fillDate;
    private BigDecimal mileage;
    private String station;
    private BigDecimal liters;
    @Column(name = "price_per_liter") private BigDecimal pricePerLiter;
    @Column(name = "total_amount") private BigDecimal totalAmount;
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
