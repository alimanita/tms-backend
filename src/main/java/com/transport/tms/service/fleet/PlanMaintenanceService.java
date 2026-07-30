package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.PlanMaintenanceRequest;
import com.transport.tms.dto.fleet.response.PlanMaintenanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanMaintenanceService {

    Page<PlanMaintenanceResponse> findAll(Pageable pageable);

    PlanMaintenanceResponse findById(Long id);

    PlanMaintenanceResponse create(PlanMaintenanceRequest request);

    PlanMaintenanceResponse update(Long id, PlanMaintenanceRequest request);

    void delete(Long id);

    List<PlanMaintenanceResponse> findByVehicule(Long vehiculeId);

    List<PlanMaintenanceResponse> findByMachine(Long machineId);

    List<PlanMaintenanceResponse> findEcheancesProches();
}