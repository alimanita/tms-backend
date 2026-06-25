package com.transport.tms.controller;

import com.transport.tms.dto.request.DriverRequest;
import com.transport.tms.dto.response.DriverResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public PageResponse<DriverResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return driverService.list(page, size);
    }

    @GetMapping("/{id}")
    public DriverResponse getById(@PathVariable Long id) {
        return driverService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return driverService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        driverService.delete(id);
    }
}
