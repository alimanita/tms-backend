package com.transport.tms.dto.response;

public record CustomerResponse(
        Long id,
        String name,
        String company,
        String phone,
        String email,
        String address,
        String city,
        String country,
        String nif,
        String taxId,
        boolean active
) {}
