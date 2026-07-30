package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.NotificationFlotteApi;
import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;
import com.transport.tms.service.fleet.NotificationFlotteService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationFlotteController implements NotificationFlotteApi {

    private final NotificationFlotteService notificationService;

    @Override
    public ResponseEntity<Page<NotificationFlotteResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(notificationService.findAll(pageable));
    }

    @Override
    public ResponseEntity<List<NotificationFlotteResponse>> findNonLues() {
        return ResponseEntity.ok(notificationService.findNonLues());
    }

    @Override
    public ResponseEntity<List<NotificationFlotteResponse>> findCritiques() {
        return ResponseEntity.ok(notificationService.findCritiques());
    }

    @Override
    public ResponseEntity<NotificationFlotteResponse> marquerLue(Long id) {
        return ResponseEntity.ok(notificationService.marquerLue(id));
    }

    @Override
    public ResponseEntity<Void> marquerToutesLues() {
        notificationService.marquerToutesLues();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> ignorer(Long id) {
        notificationService.ignorer(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Long> countNonLues() {
        return ResponseEntity.ok(notificationService.countNonLues());
    }
}