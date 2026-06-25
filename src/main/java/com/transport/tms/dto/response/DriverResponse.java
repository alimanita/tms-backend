package com.transport.tms.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DriverResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String cin,
        String phone,
        String address,
        LocalDate hireDate,
        BigDecimal salary,
        String licenseNumber,
        String licenseCategory,
        LocalDate licenseExpiry,
        boolean active
) {}
