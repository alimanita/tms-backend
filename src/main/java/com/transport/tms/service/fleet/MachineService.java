package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.MachineRequest;
import com.transport.tms.dto.fleet.request.UpdateHeuresRequest;
import com.transport.tms.dto.fleet.response.MachineResponse;
import com.transport.tms.dto.fleet.response.UpdateHeuresResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MachineService {
    
    MachineResponse create(MachineRequest request);
    
    MachineResponse update(Long id, MachineRequest request);
    
    void delete(Long id);
    
    MachineResponse getById(Long id);
    
    Page<MachineResponse> getAll(Pageable pageable);
    
    List<MachineResponse> getAllActive();
    UpdateHeuresResponse updateHeuresActuelles(Long id, UpdateHeuresRequest request);
    
}
