package com.transport.tms.dto.fleet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkOpportunityImportRequest {
    private String source;
    private List<WorkOpportunityImportItem> workOpportunities;
}
