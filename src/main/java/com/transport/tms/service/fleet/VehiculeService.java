package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehiculeService {
    VehiculeResponse create(VehiculeRequest request);
    VehiculeResponse update(Long id, VehiculeRequest request);
    VehiculeResponse findById(Long id);
    Page<VehiculeResponse> findAll(Pageable pageable);
    List<VehiculeResponse> findDisponibles();
    VehiculeResponse updateStatut(Long id, String statut);
    void delete(Long id);
}