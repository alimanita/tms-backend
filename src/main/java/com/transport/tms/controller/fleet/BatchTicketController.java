package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.BatchTicketApi;
import com.transport.tms.dto.fleet.request.BatchTicketSaveRequest;
import com.transport.tms.dto.fleet.response.BatchTicketItemResult;
import com.transport.tms.dto.fleet.response.BatchTicketSaveResult;
import com.transport.tms.service.fleet.BatchTicketOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BatchTicketController implements BatchTicketApi {

    private final BatchTicketOcrService batchTicketOcrService;

    @Override
    public ResponseEntity<List<BatchTicketItemResult>> analyzeBatch(List<MultipartFile> files) {
        return ResponseEntity.ok(batchTicketOcrService.analyzeBatch(files));
    }

    @Override
    public ResponseEntity<BatchTicketSaveResult> saveBatch(BatchTicketSaveRequest request) {
        return ResponseEntity.ok(batchTicketOcrService.saveBatch(request));
    }
}
