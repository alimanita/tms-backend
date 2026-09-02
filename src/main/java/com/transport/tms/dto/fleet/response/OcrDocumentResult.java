package com.transport.tms.dto.fleet.response;

import java.math.BigDecimal;

public record OcrDocumentResult(
        String typeDocument,
        String referenceNumber,
        String issueDate,
        String expiryDate,
        String issuer,
        BigDecimal amount,
        String notes
) {
}
