package com.transport.tms.dto.response;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String type,
        String severity,
        String title,
        String message,
        String entityType,
        Long entityId,
        boolean readFlag,
        String channel,
        Instant createdAt
) {}
