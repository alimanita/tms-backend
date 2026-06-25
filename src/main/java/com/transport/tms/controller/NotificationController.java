package com.transport.tms.controller;

import com.transport.tms.dto.request.NotificationRequest;
import com.transport.tms.dto.response.NotificationResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public PageResponse<NotificationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return notificationService.list(page, size);
    }

    @GetMapping("/{id}")
    public NotificationResponse getById(@PathVariable Long id) {
        return notificationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@Valid @RequestBody NotificationRequest request) {
        return notificationService.create(request);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        notificationService.delete(id);
    }
}
