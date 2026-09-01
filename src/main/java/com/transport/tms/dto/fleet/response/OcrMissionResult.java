package com.transport.tms.dto.fleet.response;

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
public class OcrMissionResult {
    private String title;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime plannedDeparture;
    private LocalDateTime plannedReturn;
    private BigDecimal revenue;
    private String cargoDescription;
    private String notes;
}
