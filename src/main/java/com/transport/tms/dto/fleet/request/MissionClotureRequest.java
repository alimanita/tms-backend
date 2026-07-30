package com.transport.tms.dto.fleet.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MissionClotureRequest(

    BigDecimal mileageAtReturn
) {}