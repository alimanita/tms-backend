package com.transport.tms.controller;

import com.transport.tms.dto.request.FuelRecordRequest;
import com.transport.tms.dto.response.FuelRecordResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.service.FuelRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fuel-records")
@RequiredArgsConstructor
public class FuelRecordController {

    private final FuelRecordService fuelRecordService;

    @GetMapping
    public PageResponse<FuelRecordResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return fuelRecordService.list(page, size);
    }

    @GetMapping("/my")
    public PageResponse<FuelRecordResponse> listMy(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long driverId = principal.getDriverId();
        if (driverId == null) {
            return new PageResponse<>(java.util.List.of(), 0, 0, 0L, 0);
        }
        return fuelRecordService.listByDriver(driverId, page, size);
    }

    @GetMapping("/{id}")
    public FuelRecordResponse getById(@PathVariable Long id) {
        return fuelRecordService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuelRecordResponse create(@Valid @RequestBody FuelRecordRequest request) {
        return fuelRecordService.create(request);
    }

    @PutMapping("/{id}")
    public FuelRecordResponse update(@PathVariable Long id, @Valid @RequestBody FuelRecordRequest request) {
        return fuelRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        fuelRecordService.delete(id);
    }
}
