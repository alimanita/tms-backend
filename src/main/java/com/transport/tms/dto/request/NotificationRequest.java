package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NotificationRequest(
        @NotBlank String type,
        String severity,
        @NotBlank String title,
        String message,
        String entityType,
        Long entityId,
        String channel
) {}
