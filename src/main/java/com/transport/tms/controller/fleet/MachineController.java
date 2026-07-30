package com.transport.tms.controller.fleet;

import com.transport.tms.controller.api.fleet.MachineApi;
import com.transport.tms.dto.fleet.request.MachineRequest;
import com.transport.tms.dto.fleet.request.UpdateHeuresRequest;
import com.transport.tms.dto.fleet.response.MachineResponse;
import com.transport.tms.dto.fleet.response.UpdateHeuresResponse;
import com.transport.tms.service.fleet.MachineService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MachineController implements MachineApi {

    private final MachineService machineService;

    @Override
    public ResponseEntity<Page<MachineResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(machineService.getAll(pageable));
    }

    @Override
    public ResponseEntity<MachineResponse> findById(Long id) {
        return ResponseEntity.ok(machineService.getById(id));
    }

    @Override
    public ResponseEntity<MachineResponse> create(MachineRequest request) {
        return ResponseEntity.ok(machineService.create(request));
    }

    @Override
    public ResponseEntity<MachineResponse> update(Long id, MachineRequest request) {
        return ResponseEntity.ok(machineService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        machineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<MachineResponse>> findActives() {
        return ResponseEntity.ok(machineService.getAllActive());
    }
    @Override
    public ResponseEntity<UpdateHeuresResponse> updateHeuresActuelles(Long id, UpdateHeuresRequest request) {
        return ResponseEntity.ok(machineService.updateHeuresActuelles(id, request));
    }
}
