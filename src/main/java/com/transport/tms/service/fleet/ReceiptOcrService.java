package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.response.OcrFuelResult;
import org.springframework.web.multipart.MultipartFile;

import com.transport.tms.dto.fleet.response.OcrTollResult;

public interface ReceiptOcrService {
    OcrFuelResult extractFuelData(MultipartFile image);
    OcrTollResult extractTollData(MultipartFile image);
}
