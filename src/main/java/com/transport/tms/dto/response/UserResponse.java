package com.transport.tms.dto.response;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        boolean active,
        Long driverId,
        Instant createdAt,
        List<String> roles
) {}
