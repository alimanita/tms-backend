package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOpportunityImportResult {
    private int imported;
    private int skipped;
    private int total;
}
