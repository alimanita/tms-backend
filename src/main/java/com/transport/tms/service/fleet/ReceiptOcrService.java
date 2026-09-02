package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.response.OcrFuelResult;
import com.transport.tms.dto.fleet.response.OcrMissionResult;
import org.springframework.web.multipart.MultipartFile;

import com.transport.tms.dto.fleet.response.OcrTollResult;

public interface ReceiptOcrService {
    OcrFuelResult extractFuelData(MultipartFile image);
    OcrTollResult extractTollData(MultipartFile image);
    com.transport.tms.dto.fleet.response.OcrPieceResult extractPieceData(MultipartFile image);
    OcrMissionResult extractMissionData(MultipartFile image);
    com.transport.tms.dto.fleet.response.OcrDocumentResult extractDocumentData(MultipartFile image);
}
