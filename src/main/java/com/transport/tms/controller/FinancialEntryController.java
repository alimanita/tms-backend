package com.transport.tms.controller;

import com.transport.tms.dto.request.FinancialEntryRequest;
import com.transport.tms.dto.response.FinancialEntryResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.FinancialEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/financial-entries")
@RequiredArgsConstructor
public class FinancialEntryController {

    private final FinancialEntryService financialEntryService;

    @GetMapping
    public PageResponse<FinancialEntryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return financialEntryService.list(page, size);
    }

    @GetMapping("/{id}")
    public FinancialEntryResponse getById(@PathVariable Long id) {
        return financialEntryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialEntryResponse create(@Valid @RequestBody FinancialEntryRequest request) {
        return financialEntryService.create(request);
    }

    @PutMapping("/{id}")
    public FinancialEntryResponse update(@PathVariable Long id, @Valid @RequestBody FinancialEntryRequest request) {
        return financialEntryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        financialEntryService.delete(id);
    }
}
