package com.transport.tms.dto.fleet.response;
import java.math.BigDecimal;

public record OTMainOeuvreResponse(
    Long id,
    String technicianName,
    Boolean isExternal,
    BigDecimal hoursPlanned,
    BigDecimal hoursActual,
    BigDecimal hourlyRate,
    BigDecimal totalCost
) {}