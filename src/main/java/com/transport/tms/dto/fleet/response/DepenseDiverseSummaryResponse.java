package com.transport.tms.dto.fleet.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DepenseDiverseSummaryResponse(
        BigDecimal totalAmountTTC,
        Long totalCount
) {}
