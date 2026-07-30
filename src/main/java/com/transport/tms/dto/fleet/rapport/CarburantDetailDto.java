package com.transport.tms.dto.fleet.rapport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarburantDetailDto {
    private Long id;
    private String reference;
    private LocalDateTime fillingDate;
    private String vehiculeRef;
    private String chauffeurNom;
    private String fuelType;
    private BigDecimal quantityLiters;
    private BigDecimal pricePerLiter;
    private BigDecimal coutTotal;
    private BigDecimal mileageAfter;
    private BigDecimal consumptionRate; // L/100km
}
