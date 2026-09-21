package com.transport.tms.controller.fleet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tms.controller.api.fleet.BatchTicketApi;
import com.transport.tms.dto.fleet.request.BatchTicketSaveRequest;
import com.transport.tms.dto.fleet.response.BatchTicketItemResult;
import com.transport.tms.dto.fleet.response.BatchTicketSaveResult;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.service.fleet.BatchTicketOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BatchTicketController implements BatchTicketApi {

    private final BatchTicketOcrService batchTicketOcrService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<List<BatchTicketItemResult>> analyzeBatch(List<MultipartFile> files) {
        return ResponseEntity.ok(batchTicketOcrService.analyzeBatch(files));
    }

    @Override
    public ResponseEntity<BatchTicketSaveResult> saveBatch(String dataJson, MultipartHttpServletRequest request) {
        BatchTicketSaveRequest saveRequest;
        try {
            saveRequest = objectMapper.readValue(dataJson, BatchTicketSaveRequest.class);
        } catch (Exception e) {
            log.error("Erreur lors de la désérialisation du JSON de lot", e);
            throw new InvalidOperationException("Format de données JSON invalide: " + e.getMessage());
        }

        Map<Integer, MultipartFile> proofFiles = new HashMap<>();
        Map<String, MultipartFile> fileMap = request.getFileMap();
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("file_") || key.startsWith("proof_")) {
                try {
                    int index = Integer.parseInt(key.substring(key.indexOf('_') + 1));
                    proofFiles.put(index, entry.getValue());
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    int index = Integer.parseInt(key);
                    proofFiles.put(index, entry.getValue());
                } catch (NumberFormatException ignored) {}
            }
        }

        return ResponseEntity.ok(batchTicketOcrService.saveBatch(saveRequest, proofFiles));
    }
}

