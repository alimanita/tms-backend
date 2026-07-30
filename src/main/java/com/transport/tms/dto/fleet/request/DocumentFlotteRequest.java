package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.domain.enums.TypeDocument;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDate;

public record DocumentFlotteRequest(

    @NotNull(message = "Le type de document est obligatoire")
    TypeDocument typeDocument,

    @NotNull
    DocumentFlotte.TypeEntite entityType,

    @NotNull
    Long entityId,

    @Size(max = 100)
    String referenceNumber,

    @Size(max = 200)
    String issuer,

    LocalDate issueDate,

    LocalDate expiryDate,

    @DecimalMin(value = "0.0")
    BigDecimal amount,

    String filePath,

    String fileName,

    String notes
) {}