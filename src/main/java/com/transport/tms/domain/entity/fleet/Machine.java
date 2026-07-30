package com.transport.tms.domain.entity.fleet;


import com.transport.tms.domain.enums.StatutMachine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fleet_machine")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;                   // MC-2024-001

    @Column(length = 100)
    private String numeroSerie;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(length = 100)
    private String marque;

    @Column(length = 100)
    private String modele;

    @Column(length = 50)
    private String categorie;                   // COMPRESSEUR | GRUE | GÉNÉRATEUR | ...

    private LocalDate dateAchat;

    @Column(precision = 15, scale = 2)
    private BigDecimal prixAchat;

    @Column(length = 20)
    private String unitesPuissance;             // KW | CV | HP

    @Column(precision = 10, scale = 2)
    private BigDecimal valeurPuissance;

    @Column(precision = 12, scale = 2)
    private BigDecimal heuresInitiales;

    @Column(precision = 12, scale = 2)
    private BigDecimal heuresActuelles;

    @Column(length = 200)
    private String localisation;               // Chantier, atelier...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutMachine statut = StatutMachine.DISPONIBLE;

    @Column(precision = 5, scale = 2)
    private BigDecimal tauxDisponibilite;      // % calculé périodiquement

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    private Boolean actif = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MachineMaintenanceRule> reglesMaintenance = new ArrayList<>();

}