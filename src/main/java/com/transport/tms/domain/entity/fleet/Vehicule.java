package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.domain.enums.TypeCarburant;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "fleet_vehicule")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(unique = true, nullable = false)
    private String immatriculation;

    private String marque;
    private String modele;
    private Integer annee;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeCarburant typeCarburant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutVehicule statut = StatutVehicule.DISPONIBLE;

    @Column(precision = 12, scale = 2)
    private BigDecimal kilometrageActuel;

    @Column(precision = 10, scale = 2)
    private BigDecimal capaciteReservoir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id")
    private Chauffeur chauffeurAffecte;

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

    @Column(name = "identreprise")
    private Integer idEntreprise;
}