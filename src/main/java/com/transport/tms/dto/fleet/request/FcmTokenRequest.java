package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenRequest(
        @NotBlank(message = "Le token est obligatoire")
        String token,

        String device
) {}