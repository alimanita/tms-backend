package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.StatutOffre;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_opportunity")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WorkOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true)
    private String externalId;

    @Column(length = 80)
    @Builder.Default
    private String source = "IMPORT";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutOffre statut = StatutOffre.DISPONIBLE;

    @Column(length = 150)
    private String departureCity;

    private Double departureLat;
    private Double departureLng;

    @Column(length = 150)
    private String arrivalCity;

    private Double arrivalLat;
    private Double arrivalLng;

    private LocalDateTime firstPickupTime;
    private LocalDateTime lastDeliveryTime;

    @Column(precision = 15, scale = 2)
    private BigDecimal payoutValue;

    @Column(length = 10)
    @Builder.Default
    private String payoutUnit = "EUR";

    private Double distanceKm;

    @Column(length = 200)
    private String cargoType;

    @Column(precision = 10, scale = 2)
    private BigDecimal cargoWeightKg;

    @Column(precision = 10, scale = 2)
    private BigDecimal cargoVolumeM3;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String rawJson;

    private Long entrepriseId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
