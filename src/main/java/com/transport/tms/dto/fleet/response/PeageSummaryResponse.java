package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeageSummaryResponse {
    private BigDecimal totalAmountTTC;
    private BigDecimal totalAmountHT;
    private Long totalCount;
}