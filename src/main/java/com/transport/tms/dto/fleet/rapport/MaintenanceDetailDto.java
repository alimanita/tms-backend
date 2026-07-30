package com.transport.tms.dto.fleet.rapport;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceDetailDto {
    private Long id;
    private String reference;
    private String entityRef;           // Nom machine ou véhicule
    private OrdreTravail.TypeEntite entityType;
    private String typeMaintenance;
    private String priorite;
    private String statut;
    private LocalDate scheduledDate;
    private LocalDateTime completedAt;
    private BigDecimal coutMainOeuvre;
    private BigDecimal coutPieces;
    private BigDecimal coutTotal;
}
