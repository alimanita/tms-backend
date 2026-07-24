package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.MachineStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "fleet_machine")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Machine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String reference;
    @Column(name = "serial_number") private String serialNumber;
    @Column(nullable = false) private String name;
    private String brand;
    private String model;
    private String category;
    @Column(name = "purchase_date") private LocalDate purchaseDate;
    @Column(name = "purchase_price") private BigDecimal purchasePrice;
    @Column(name = "power_unit") private String powerUnit;
    @Column(name = "power_value") private BigDecimal powerValue;
    @Column(name = "initial_hours") @Builder.Default private BigDecimal initialHours = BigDecimal.ZERO;
    @Column(name = "current_hours") @Builder.Default private BigDecimal currentHours = BigDecimal.ZERO;
    private String location;
    @Enumerated(EnumType.STRING) @Builder.Default private MachineStatus status = MachineStatus.AVAILABLE;
    private String notes;
    @Builder.Default private boolean active = true;
    @Builder.Default @Column(name = "created_at", updatable = false) private Instant createdAt = Instant.now();
}
