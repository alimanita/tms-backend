package com.transport.tms.dto.response;

import com.transport.tms.domain.enums.DocumentType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FleetDocumentResponse(
        Long id,
        Long vehicleId,
        String vehicleRegistration,
        Long driverId,
        String driverName,
        DocumentType documentType,
        String referenceNumber,
        String issuer,
        LocalDate issueDate,
        LocalDate expiryDate,
        BigDecimal amount,
        String filePath,
        String fileName,
        String status,
        String notes,
        Instant createdAt
) {}
