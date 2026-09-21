package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.response.BatchTicketItemResult;
import com.transport.tms.dto.fleet.response.BatchTicketSaveResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@RequestMapping("api/v1/fleet/tickets/batch")
public interface BatchTicketApi {

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<List<BatchTicketItemResult>> analyzeBatch(
            @RequestParam("files") List<MultipartFile> files);

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<BatchTicketSaveResult> saveBatch(
            @RequestPart("data") String dataJson,
            MultipartHttpServletRequest request);
}

