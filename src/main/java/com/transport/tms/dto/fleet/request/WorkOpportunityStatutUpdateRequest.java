package com.transport.tms.dto.fleet.request;

import com.transport.tms.domain.enums.StatutOffre;
import lombok.Data;

@Data
public class WorkOpportunityStatutUpdateRequest {
    private StatutOffre statut;
}
