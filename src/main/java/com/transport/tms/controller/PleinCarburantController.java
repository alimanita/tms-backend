package com.transport.tms.controller;

import com.transport.tms.dto.request.FuelRecordRequest;
import com.transport.tms.dto.response.FuelRecordResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.PleinCarburantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fleet/pleins-carburant")
@RequiredArgsConstructor
public class PleinCarburantController {

    private final PleinCarburantService pleinCarburantService;

    @GetMapping
    public PageResponse<FuelRecordResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return pleinCarburantService.list(page, size);
    }

    @GetMapping("/chauffeur/{chauffeurId}")
    public PageResponse<FuelRecordResponse> listByChauffeur(
            @PathVariable Long chauffeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return pleinCarburantService.listByChauffeur(chauffeurId, page, size);
    }

    @GetMapping("/{id}")
    public FuelRecordResponse getById(@PathVariable Long id) {
        return pleinCarburantService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuelRecordResponse create(@Valid @RequestBody FuelRecordRequest request) {
        return pleinCarburantService.create(request);
    }

    @PutMapping("/{id}")
    public FuelRecordResponse update(@PathVariable Long id, @Valid @RequestBody FuelRecordRequest request) {
        return pleinCarburantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pleinCarburantService.delete(id);
    }
}