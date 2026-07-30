package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.TypeActionMaintenance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fleet_machine_maintenance_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineMaintenanceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 10)
    private String code;                  // A, B, C, D, E, F...

    @Column(length = 200)
    private String description;           // "Vérifier niveau et remplir huile HM 32"

    @Enumerated(EnumType.STRING)
    private TypeActionMaintenance typeAction; // LUBRIFICATION | VIDANGE | VERIFICATION | SERRAGE

    private Integer intervalleHeures;     // 200, 2000, 5000...

    private Integer intervalleJours;      // pour "chaque lundi matin" par ex.

    @Column(length = 100)
    private String consommable;           // "Graisse lithium 2#", "Huile CKC 150"

    @Column(precision = 10, scale = 2)
    private BigDecimal quantite;          // 1kg

    @Column(length = 20)
    private String uniteQuantite;         // KG, L

    private BigDecimal dernieresHeuresEffectuees;
    private LocalDate derniereDateEffectuee;

    @Builder.Default
    private Boolean actif = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

}