package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "vehicle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String registration;
    private String vin;
    private String brand;
    private String model;
    private Integer year;
    @Column(name = "vehicle_type") private String vehicleType;
    @Column(name = "payload_kg") private BigDecimal payloadKg;
    @Column(name = "current_mileage", nullable = false) private BigDecimal currentMileage;
    @Column(name = "acquisition_date") private LocalDate acquisitionDate;
    @Column(name = "insurance_expiry") private LocalDate insuranceExpiry;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private VehicleStatus status;
    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}
