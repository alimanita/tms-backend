package com.transport.tms.controller;

import com.transport.tms.dto.request.MaintenanceRecordRequest;
import com.transport.tms.dto.response.MaintenanceRecordResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.MaintenanceRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenance-records")
@RequiredArgsConstructor
public class MaintenanceRecordController {

    private final MaintenanceRecordService maintenanceRecordService;

    @GetMapping
    public PageResponse<MaintenanceRecordResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return maintenanceRecordService.list(page, size);
    }

    @GetMapping("/{id}")
    public MaintenanceRecordResponse getById(@PathVariable Long id) {
        return maintenanceRecordService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceRecordResponse create(@Valid @RequestBody MaintenanceRecordRequest request) {
        return maintenanceRecordService.create(request);
    }

    @PutMapping("/{id}")
    public MaintenanceRecordResponse update(@PathVariable Long id, @Valid @RequestBody MaintenanceRecordRequest request) {
        return maintenanceRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        maintenanceRecordService.delete(id);
    }
}
