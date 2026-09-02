package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "document")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class DocumentFlotte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @JoinColumn(name = "document_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeDocument typeDocument;

    @Column(name = "entity_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntite entityType;

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(length = 200)
    private String issuer;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_name", length = 200)
    private String fileName;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private StatutDocument status = StatutDocument.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isExpiringSoon(int alertDays) {
        if (expiryDate == null) return false;
        return LocalDate.now().plusDays(alertDays).isAfter(expiryDate)
                && status == StatutDocument.ACTIVE;
    }

    public boolean isExpired() {
        if (expiryDate == null) return false;
        return LocalDate.now().isAfter(expiryDate);
    }

    public enum TypeEntite { VEHICLE, MACHINE, DRIVER }
    public enum StatutDocument { ACTIVE, EXPIRED, CANCELLED }
}