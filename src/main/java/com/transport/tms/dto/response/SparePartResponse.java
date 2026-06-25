package com.transport.tms.dto.response;

import java.math.BigDecimal;

public record SparePartResponse(
        Long id,
        String reference,
        String designation,
        String category,
        String supplier,
        BigDecimal purchasePrice,
        BigDecimal stockQty,
        BigDecimal minThreshold,
        boolean active
) {}
