package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;

public record TvaReportResponse(
    BigDecimal totalHT,
    BigDecimal totalTTC,
    BigDecimal totalTva,
    BigDecimal totalRecoverableTva,
    BigDecimal totalAccise
) {}
