package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.BatchTicketSaveRequest;
import com.transport.tms.dto.fleet.response.BatchTicketItemResult;
import com.transport.tms.dto.fleet.response.BatchTicketSaveResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BatchTicketOcrService {
    List<BatchTicketItemResult> analyzeBatch(List<MultipartFile> files);
    BatchTicketSaveResult saveBatch(BatchTicketSaveRequest request);
}
