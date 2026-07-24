package com.transport.tms.controller;

import com.transport.tms.dto.request.FleetDocumentRequest;
import com.transport.tms.dto.response.FleetDocumentResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.FleetDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet-documents")
@RequiredArgsConstructor
public class FleetDocumentController {

    private final FleetDocumentService service;

    @GetMapping
    public PageResponse<FleetDocumentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public FleetDocumentResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<FleetDocumentResponse> getByVehicle(@PathVariable Long vehicleId) {
        return service.getByVehicle(vehicleId);
    }

    @GetMapping("/driver/{driverId}")
    public List<FleetDocumentResponse> getByDriver(@PathVariable Long driverId) {
        return service.getByDriver(driverId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FleetDocumentResponse create(@Valid @RequestBody FleetDocumentRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public FleetDocumentResponse update(@PathVariable Long id, @Valid @RequestBody FleetDocumentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
