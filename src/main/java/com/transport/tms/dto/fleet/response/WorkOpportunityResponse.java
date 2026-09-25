package com.transport.tms.dto.fleet.response;

import com.transport.tms.domain.enums.StatutOffre;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WorkOpportunityResponse {
    private Long id;
    private String externalId;
    private String source;
    private StatutOffre statut;
    
    private String departureCity;
    private Double departureLat;
    private Double departureLng;
    
    private String arrivalCity;
    private Double arrivalLat;
    private Double arrivalLng;
    
    private LocalDateTime firstPickupTime;
    private LocalDateTime lastDeliveryTime;
    
    private BigDecimal payoutValue;
    private String payoutUnit;
    private Double distanceKm;
    
    private String cargoType;
    private BigDecimal cargoWeightKg;
    private BigDecimal cargoVolumeM3;
    
    private String notes;
    private Long entrepriseId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed field for nearby results
    private Double emptyKm;
}
