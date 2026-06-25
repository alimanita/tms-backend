package com.transport.tms.controller;

import com.transport.tms.dto.request.AmazonPurchaseRequest;
import com.transport.tms.dto.response.AmazonPurchaseResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.AmazonPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amazon-purchases")
@RequiredArgsConstructor
public class AmazonPurchaseController {

    private final AmazonPurchaseService amazonPurchaseService;

    @GetMapping
    public PageResponse<AmazonPurchaseResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return amazonPurchaseService.list(page, size);
    }

    @GetMapping("/{id}")
    public AmazonPurchaseResponse getById(@PathVariable Long id) {
        return amazonPurchaseService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmazonPurchaseResponse create(@Valid @RequestBody AmazonPurchaseRequest request) {
        return amazonPurchaseService.create(request);
    }

    @PutMapping("/{id}")
    public AmazonPurchaseResponse update(@PathVariable Long id, @Valid @RequestBody AmazonPurchaseRequest request) {
        return amazonPurchaseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        amazonPurchaseService.delete(id);
    }
}
