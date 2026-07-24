package com.transport.tms.dto.request;

import com.transport.tms.domain.enums.DocumentType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FleetDocumentRequest(
        Long vehicleId,
        Long driverId,
        @NotNull DocumentType documentType,
        String referenceNumber,
        String issuer,
        LocalDate issueDate,
        LocalDate expiryDate,
        BigDecimal amount,
        String status,
        String notes
) {}
