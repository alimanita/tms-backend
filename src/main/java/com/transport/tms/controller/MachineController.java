package com.transport.tms.controller;

import com.transport.tms.dto.request.MachineRequest;
import com.transport.tms.dto.request.UpdateMachineHoursRequest;
import com.transport.tms.dto.response.MachineResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.MachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    public PageResponse<MachineResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return machineService.list(page, size);
    }

    @GetMapping("/active")
    public List<MachineResponse> listActive() {
        return machineService.listActive();
    }

    @GetMapping("/{id}")
    public MachineResponse getById(@PathVariable Long id) {
        return machineService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MachineResponse create(@Valid @RequestBody MachineRequest request) {
        return machineService.create(request);
    }

    @PutMapping("/{id}")
    public MachineResponse update(@PathVariable Long id, @Valid @RequestBody MachineRequest request) {
        return machineService.update(id, request);
    }

    @PatchMapping("/{id}/hours")
    public MachineResponse updateHours(@PathVariable Long id, @Valid @RequestBody UpdateMachineHoursRequest request) {
        return machineService.updateHours(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        machineService.delete(id);
    }
}
