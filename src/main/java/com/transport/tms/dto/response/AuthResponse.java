package com.transport.tms.dto.response;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInMs,
        UserResponse user
) {
    public record UserResponse(
            Long id,
            String username,
            String fullName,
            Long entrepriseId,

            List<String> roles
    ) {}
}
