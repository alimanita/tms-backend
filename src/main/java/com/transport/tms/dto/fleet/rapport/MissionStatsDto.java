package com.transport.tms.dto.fleet.rapport;

import lombok.Data;
import java.util.Map;

@Data
public class MissionStatsDto {
    private Map<String, Long> missionsByDriver;
    private Map<String, Long> missionsByStatus;
    private Map<String, java.math.BigDecimal> mileageByDriver;
}
