package com.transport.tms.dto.fleet.response;


import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.domain.enums.TypeDocument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record DocumentFlotteResponse(
    Long id,
    TypeDocument typeDocument,
    String typeDocumentLabel,
    DocumentFlotte.TypeEntite entityType,
    Long entityId,
    String entityRef,
    String referenceNumber,
    String issuer,
    LocalDate issueDate,
    LocalDate expiryDate,
    BigDecimal amount,
    String filePath,
    String fileName,
    DocumentFlotte.StatutDocument status,
    Boolean isExpired,
    Boolean isExpiringSoon,       // selon alertDays du TypeDocument
    Long joursRestants,           // null si pas d'expiry
    String notes,
    LocalDateTime createdAt
) {}