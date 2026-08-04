package com.transport.tms.dto.fleet.rapport;

import lombok.Data;
import java.util.Map;

@Data
public class AmazonStatsDto {
    private Map<String, java.math.BigDecimal> expensesByMonth;
    private Map<String, java.math.BigDecimal> expensesBySupplier;
}
