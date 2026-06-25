package com.transport.tms.controller;

import com.transport.tms.dto.request.CustomerOrderRequest;
import com.transport.tms.dto.response.CustomerOrderResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.CustomerOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer-orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @GetMapping
    public PageResponse<CustomerOrderResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return customerOrderService.list(page, size);
    }

    @GetMapping("/{id}")
    public CustomerOrderResponse getById(@PathVariable Long id) {
        return customerOrderService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrderResponse create(@Valid @RequestBody CustomerOrderRequest request) {
        return customerOrderService.create(request);
    }

    @PutMapping("/{id}")
    public CustomerOrderResponse update(@PathVariable Long id, @Valid @RequestBody CustomerOrderRequest request) {
        return customerOrderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerOrderService.delete(id);
    }
}
