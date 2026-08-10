package com.transport.tms.controller.fleet;

import com.transport.tms.dto.fleet.response.GlobalTvaReportDto;
import com.transport.tms.service.fleet.GlobalTvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fleet/rapports/tva")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapportTvaController {

    private final GlobalTvaService globalTvaService;

    @GetMapping("/global")
    public ResponseEntity<GlobalTvaReportDto> getGlobalTvaReport(
            @org.springframework.web.bind.annotation.RequestParam(required = false, defaultValue = "all") String periodMode,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer year,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(globalTvaService.getGlobalReport(periodMode, year, month));
    }
}

