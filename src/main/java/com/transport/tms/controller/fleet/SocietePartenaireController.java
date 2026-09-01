package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.SocietePartenaireApi;
import com.transport.tms.dto.fleet.request.SocietePartenaireRequest;
import com.transport.tms.dto.fleet.response.SocietePartenaireResponse;
import com.transport.tms.service.fleet.SocietePartenaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SocietePartenaireController implements SocietePartenaireApi {

    private final SocietePartenaireService service;

    @Override
    public ResponseEntity<SocietePartenaireResponse> create(SocietePartenaireRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<SocietePartenaireResponse> update(Long id, SocietePartenaireRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Override
    public ResponseEntity<SocietePartenaireResponse> findById(Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    public ResponseEntity<Page<SocietePartenaireResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Override
    public ResponseEntity<List<SocietePartenaireResponse>> findAllActive() {
        return ResponseEntity.ok(service.findAllActive());
    }
}
