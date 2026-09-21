package com.transport.tms.dto.fleet.request;

import lombok.Data;

import java.util.List;

@Data
public class BatchTicketSaveRequest {
    private List<BatchTicketSaveItem> items;
}
