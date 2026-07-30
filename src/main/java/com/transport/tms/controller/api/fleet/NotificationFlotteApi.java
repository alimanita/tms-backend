package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("api/v1/fleet/notifications")
public interface NotificationFlotteApi {

    @GetMapping
    ResponseEntity<Page<NotificationFlotteResponse>> findAll(Pageable pageable);

    @GetMapping("/non-lues")
    ResponseEntity<List<NotificationFlotteResponse>> findNonLues();

    @GetMapping("/critiques")
    ResponseEntity<List<NotificationFlotteResponse>> findCritiques();

    @PatchMapping("/{id}/lire")
    ResponseEntity<NotificationFlotteResponse> marquerLue(@PathVariable Long id);

    @PatchMapping("/lire-toutes")
    ResponseEntity<Void> marquerToutesLues();

    @PatchMapping("/{id}/ignorer")
    ResponseEntity<Void> ignorer(@PathVariable Long id);

    @GetMapping("/count-non-lues")
    ResponseEntity<Long> countNonLues();
}