package com.transport.tms.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DriverRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String cin,
        String phone,
        String address,
        LocalDate hireDate,
        BigDecimal salary,
        String licenseNumber,
        String licenseCategory,
        LocalDate licenseExpiry
) {}
