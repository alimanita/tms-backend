package com.transport.tms.controller;

import com.transport.tms.dto.request.MaintenancePlanRequest;
import com.transport.tms.dto.response.MaintenancePlanResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.MaintenancePlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance-plans")
@RequiredArgsConstructor
public class MaintenancePlanController {

    private final MaintenancePlanService service;

    @GetMapping
    public PageResponse<MaintenancePlanResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.list(page, size);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<MaintenancePlanResponse> getByVehicle(@PathVariable Long vehicleId) {
        return service.getByVehicle(vehicleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenancePlanResponse create(@Valid @RequestBody MaintenancePlanRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public MaintenancePlanResponse update(@PathVariable Long id, @Valid @RequestBody MaintenancePlanRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
