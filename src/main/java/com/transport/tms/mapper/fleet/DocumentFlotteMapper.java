package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.dto.fleet.request.DocumentFlotteRequest;
import com.transport.tms.dto.fleet.response.DocumentFlotteResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DocumentFlotteMapper {

    public DocumentFlotte toEntity(DocumentFlotteRequest request) {
        DocumentFlotte doc = new DocumentFlotte();
        doc.setTypeDocument(request.typeDocument());
        doc.setEntityType(request.entityType());
        doc.setEntityId(request.entityId());
        doc.setReferenceNumber(request.referenceNumber());
        doc.setIssuer(request.issuer());
        doc.setIssueDate(request.issueDate());
        doc.setExpiryDate(request.expiryDate());
        doc.setAmount(request.amount());
        doc.setFilePath(request.filePath());
        doc.setFileName(request.fileName());
        doc.setNotes(request.notes());
        doc.setStatus(DocumentFlotte.StatutDocument.ACTIVE);
        return doc;
    }

    public void updateEntity(DocumentFlotte doc, DocumentFlotteRequest request) {
        doc.setTypeDocument(request.typeDocument());
        doc.setReferenceNumber(request.referenceNumber());
        doc.setIssuer(request.issuer());
        doc.setIssueDate(request.issueDate());
        doc.setExpiryDate(request.expiryDate());
        doc.setAmount(request.amount());
        doc.setFilePath(request.filePath());
        doc.setFileName(request.fileName());
        doc.setNotes(request.notes());
    }

    public DocumentFlotteResponse toResponse(DocumentFlotte doc, String entityRef) {
        Long joursRestants = null;
        boolean isExpiringSoon = false;

        if (doc.getExpiryDate() != null) {
            joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), doc.getExpiryDate());
            // Alerte si < 30 jours
            isExpiringSoon = doc.isExpiringSoon(30);
        }

        return new DocumentFlotteResponse(
                doc.getId(),
                doc.getTypeDocument(),
                doc.getTypeDocument() != null ? doc.getTypeDocument().name() : null,
                doc.getEntityType(),
                doc.getEntityId(),
                entityRef,
                doc.getReferenceNumber(),
                doc.getIssuer(),
                doc.getIssueDate(),
                doc.getExpiryDate(),
                doc.getAmount(),
                doc.getFilePath(),
                doc.getFileName(),
                doc.getStatus(),
                doc.isExpired(),
                isExpiringSoon,
                joursRestants,
                doc.getNotes(),
                doc.getCreatedAt()
        );
    }
}