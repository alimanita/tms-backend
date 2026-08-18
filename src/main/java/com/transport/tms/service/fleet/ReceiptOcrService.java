package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.response.OcrFuelResult;
import org.springframework.web.multipart.MultipartFile;

public interface ReceiptOcrService {
    OcrFuelResult extractFuelData(MultipartFile image);
}
