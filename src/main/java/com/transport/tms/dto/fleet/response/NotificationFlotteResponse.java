package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.entity.fleet.NotificationFlotte;

import java.time.LocalDate;
import java.time.LocalDateTime;
public record NotificationFlotteResponse(
        Long id,
        NotificationFlotte.TypeNotification type,
        NotificationFlotte.Severite severity,
        NotificationFlotte.TypeEntite entityType,
        Long entityId,
        String entityRef,
        String title,
        String message,
        LocalDate dueDate,
        Boolean isRead,
        Boolean isDismissed,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {}