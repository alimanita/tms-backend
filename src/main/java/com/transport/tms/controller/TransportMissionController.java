package com.transport.tms.controller;

import com.transport.tms.dto.request.MissionExpenseRequest;
import com.transport.tms.dto.request.TransportMissionRequest;
import com.transport.tms.dto.response.MaintenanceRecordResponse;
import com.transport.tms.dto.response.MissionExpenseResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.TransportMissionResponse;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.service.MaintenanceRecordService;
import com.transport.tms.service.MissionExpenseService;
import com.transport.tms.service.TransportMissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fleet/missions")
@RequiredArgsConstructor
public class TransportMissionController {

    private final TransportMissionService transportMissionService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final MissionExpenseService missionExpenseService;

    @GetMapping
    public PageResponse<TransportMissionResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return transportMissionService.list(page, size);
    }

    @GetMapping("/mes-missions")
    public PageResponse<TransportMissionResponse> listMy(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long driverId = principal.getDriverId();
        if (driverId == null) {
            return new PageResponse<>(java.util.List.of(), 0, 0, 0L, 0);
        }
        return transportMissionService.listByDriver(driverId, page, size);
    }

    @GetMapping("/my/maintenance")
    public PageResponse<MaintenanceRecordResponse> listMyMaintenance(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long driverId = principal.getDriverId();
        if (driverId == null) {
            return new PageResponse<>(java.util.List.of(), 0, 0, 0L, 0);
        }
        java.util.List<Long> vehicleIds = transportMissionService.findVehicleIdsByDriverId(driverId);
        return maintenanceRecordService.listByVehicleIds(vehicleIds, page, size);
    }

    @GetMapping("/{id}")
    public TransportMissionResponse getById(@PathVariable Long id) {
        return transportMissionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransportMissionResponse create(@Valid @RequestBody TransportMissionRequest request) {
        return transportMissionService.create(request);
    }

    @PutMapping("/{id}")
    public TransportMissionResponse update(@PathVariable Long id, @Valid @RequestBody TransportMissionRequest request) {
        return transportMissionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transportMissionService.delete(id);
    }

    @PatchMapping("/{id}/start")
    public TransportMissionResponse start(@PathVariable Long id) {
        return transportMissionService.start(id);
    }

    @PatchMapping("/{id}/complete")
    public TransportMissionResponse complete(@PathVariable Long id) {
        return transportMissionService.complete(id);
    }

    @PatchMapping("/{id}/cancel")
    public TransportMissionResponse cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return transportMissionService.cancel(id, reason);
    }

    @GetMapping("/{id}/expenses")
    public java.util.List<MissionExpenseResponse> listExpenses(@PathVariable Long id) {
        return missionExpenseService.listByMission(id);
    }

    @PostMapping("/{id}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public MissionExpenseResponse addExpense(@PathVariable Long id, @Valid @RequestBody MissionExpenseRequest request) {
        return missionExpenseService.add(id, request);
    }

    @DeleteMapping("/{id}/expenses/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeExpense(@PathVariable Long id, @PathVariable Long expenseId) {
        missionExpenseService.remove(id, expenseId);
    }
}
