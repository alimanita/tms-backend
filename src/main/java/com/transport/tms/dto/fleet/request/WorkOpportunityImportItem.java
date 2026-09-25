package com.transport.tms.dto.fleet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkOpportunityImportItem {
    
    private String id; // Source external ID
    private String firstPickupTime;
    private String lastDeliveryTime;
    private PayoutDto payout;
    private LocationDto startLocation;
    private LocationDto endLocation;
    private DistanceDto totalDistance;
    private String cargoType;
    private BigDecimal cargoWeightKg;
    private BigDecimal cargoVolumeM3;
    private String notes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayoutDto {
        private BigDecimal value;
        private String unit;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationDto {
        private String city;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DistanceDto {
        private Double value;
    }
}
