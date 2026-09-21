package com.transport.tms.dto.fleet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchTicketSaveResult {
    private int savedCount;
    private int failedCount;
    private List<BatchTicketSaveItemResult> results;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchTicketSaveItemResult {
        private int ticketIndex;
        private String ticketType;
        private Long savedId;
        private boolean success;
        private String errorMessage;
    }
}
