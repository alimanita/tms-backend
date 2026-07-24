package com.transport.tms.controller;

import com.transport.tms.domain.enums.WorkOrderEntityType;
import com.transport.tms.domain.enums.WorkOrderStatus;
import com.transport.tms.dto.request.WorkOrderRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.WorkOrderResponse;
import com.transport.tms.service.WorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/fleet/ordres-travail")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @GetMapping
    public PageResponse<WorkOrderResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) WorkOrderStatus status) {
        return workOrderService.list(page, size, status);
    }

    @GetMapping("/{id}")
    public WorkOrderResponse getById(@PathVariable Long id) {
        return workOrderService.getById(id);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public List<WorkOrderResponse> listByEntity(
            @PathVariable WorkOrderEntityType entityType,
            @PathVariable Long entityId) {
        return workOrderService.listByEntity(entityType, entityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrderResponse create(@Valid @RequestBody WorkOrderRequest request) {
        return workOrderService.create(request);
    }

    @PutMapping("/{id}")
    public WorkOrderResponse update(@PathVariable Long id, @Valid @RequestBody WorkOrderRequest request) {
        return workOrderService.update(id, request);
    }

    @PatchMapping("/{id}/start")
    public WorkOrderResponse start(@PathVariable Long id) {
        return workOrderService.start(id);
    }

    @PatchMapping("/{id}/complete")
    public WorkOrderResponse complete(@PathVariable Long id) {
        return workOrderService.complete(id);
    }

    @PatchMapping("/{id}/cancel")
    public WorkOrderResponse cancel(@PathVariable Long id) {
        return workOrderService.cancel(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workOrderService.delete(id);
    }
}
