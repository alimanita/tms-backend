package com.transport.tms.controller;

import com.transport.tms.dto.request.OilChangeRequest;
import com.transport.tms.dto.response.OilChangeResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.OilChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/oil-changes")
@RequiredArgsConstructor
public class OilChangeController {

    private final OilChangeService service;

    @GetMapping
    public PageResponse<OilChangeResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @GetMapping("/{id}")
    public OilChangeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<OilChangeResponse> getByVehicle(@PathVariable Long vehicleId) {
        return service.getByVehicle(vehicleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OilChangeResponse create(@Valid @RequestBody OilChangeRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public OilChangeResponse update(@PathVariable Long id, @Valid @RequestBody OilChangeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
