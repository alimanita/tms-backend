package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserRequest(
        @NotBlank @Size(min = 3, max = 80) String username,
        String password,           // nullable on update
        @NotBlank @Size(max = 150) String fullName,
        String email,
        String phone,
        boolean active,
        Long driverId,
        List<String> roleCodes     // e.g. ["DRIVER"], ["MANAGER"]
) {}
