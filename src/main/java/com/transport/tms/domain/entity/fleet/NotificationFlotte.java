package com.transport.tms.domain.entity.fleet;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "notification")
@Getter @Setter @NoArgsConstructor
public class NotificationFlotte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TypeNotification type;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Severite severity = Severite.INFO;

    @Column(name = "entity_type", length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntite entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_ref", length = 100)
    private String entityRef;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "is_dismissed")
    private Boolean isDismissed = false;

    @Column(name = "read_by")
    private Long readBy;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "fcm_sent")
    private Boolean fcmSent = false;

    @Column(name = "fcm_message_id", length = 200)
    private String fcmMessageId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public void marquerLue(Long userId) {
        this.isRead = true;
        this.readBy = userId;
        this.readAt = LocalDateTime.now();
    }

    public enum TypeNotification {
        MAINTENANCE_DUE, MAINTENANCE_OVERDUE,
        INSURANCE_EXPIRING, INSURANCE_EXPIRED,
        TECH_CONTROL_DUE, DOCUMENT_EXPIRING,
        LICENSE_EXPIRING, LICENSE_EXPIRED, MEDICAL_EXPIRING,
        LOW_STOCK_PART, OUT_OF_STOCK_PART,
        OIL_CHANGE_DUE, HIGH_FUEL_CONSUMPTION,
        MISSION_APPROVAL_NEEDED, MISSION_APPROVED, MISSION_DELAYED
    }

    public enum Severite { INFO, WARNING, CRITICAL }

    public enum TypeEntite { VEHICLE, MACHINE, DRIVER,MAINTENANCE_RULE}
}