package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateHeuresResponse(
        Long id,
        String nom,
        BigDecimal anciennesHeures,
        BigDecimal heuresActuelles,
        LocalDateTime updatedAt
) {}