package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "driver")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Driver {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "first_name", nullable = false) private String firstName;
    @Column(name = "last_name", nullable = false) private String lastName;
    private String cin;
    private String phone;
    private String address;
    @Column(name = "hire_date") private LocalDate hireDate;
    private BigDecimal salary;
    @Column(name = "license_number") private String licenseNumber;
    @Column(name = "license_category") private String licenseCategory;
    @Column(name = "license_expiry") private LocalDate licenseExpiry;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
   private Instant createdAt = Instant.now();

    public String getFullName() { return firstName + " " + lastName; }
}
