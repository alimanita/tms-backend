package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.PneuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PneuService {
    
    PneuResponse create(PneuRequest request);
    
    PneuResponse update(Long id, PneuRequest request);
    
    void delete(Long id);
    
    PneuResponse getById(Long id);
    
    Page<PneuResponse> getAll(Pageable pageable);
    
    List<PneuResponse> getAllActive();
    
}