package com.transport.tms.controller;

import com.transport.tms.dto.request.VehicleRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.VehicleResponse;
import com.transport.tms.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public PageResponse<VehicleResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return vehicleService.list(page, size);
    }

    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable Long id) {
        return vehicleService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }
}
