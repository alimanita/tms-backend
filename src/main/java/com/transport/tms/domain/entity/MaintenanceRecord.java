package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "maintenance_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne @JoinColumn(name = "vehicle_id") private Vehicle vehicle;
    @Column(name = "maintenance_type") private String maintenanceType;
    @Column(name = "maintenance_date") private LocalDate maintenanceDate;
    private BigDecimal mileage;
    private BigDecimal cost;
    private String supplier;
    @Column(name = "next_due_date") private LocalDate nextDueDate;
    @Column(name = "next_due_mileage") private BigDecimal nextDueMileage;
}
