package com.transport.tms.dto.fleet.response;



import com.transport.tms.domain.entity.fleet.DepenseMission;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepenseMissionResponse(
    Long id,
    Long missionId,
    String missionReference,
    DepenseMission.TypeDepense expenseType,
    BigDecimal montant,
    String currency,
    LocalDateTime expenseDate,
    String description,
    String receiptPath,
    Boolean isReimbursable,
    LocalDateTime createdAt
) {}