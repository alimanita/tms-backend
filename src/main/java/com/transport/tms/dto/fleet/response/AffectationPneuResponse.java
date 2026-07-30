package com.transport.tms.dto.fleet.response;

import com.transport.tms.domain.entity.fleet.AffectationPneu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AffectationPneuResponse(
        Long id,
        Long pneuId,
        String pneuSerialNumber,
        String pneuBrand,
        String pneuSize,
        Long vehiculeId,
        String vehiculeRef,
        AffectationPneu.PositionPneu position,
        LocalDate mountDate,
        BigDecimal mountMileage,
        LocalDate unmountDate,
        BigDecimal unmountMileage,
        BigDecimal kmUsed,
        AffectationPneu.RaisonDemontage reasonUnmount,
        String notes,
        LocalDateTime createdAt
) {}