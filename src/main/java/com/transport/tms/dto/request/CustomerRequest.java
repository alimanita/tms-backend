package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String name,
        String company,
        String phone,
        String email,
        String address,
        String city,
        String country,
        String nif,
        String taxId
) {}
