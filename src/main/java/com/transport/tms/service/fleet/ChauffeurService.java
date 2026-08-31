package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.ChauffeurConfigRequest;
import com.transport.tms.dto.fleet.request.ChauffeurRequest;
import com.transport.tms.dto.fleet.response.ChauffeurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChauffeurService {
    
    ChauffeurResponse create(ChauffeurRequest request);
    ChauffeurResponse findMe();
    ChauffeurResponse update(Long id, ChauffeurRequest request);
    
    void delete(Long id);
    
    ChauffeurResponse getById(Long id);
    
    Page<ChauffeurResponse> getAll(Pageable pageable);
    
    List<ChauffeurResponse> getAllActive();

    ChauffeurResponse toggleActif(Long id);

    // Paramétrage de la visibilité
    List<ChauffeurResponse> updateSettings(ChauffeurConfigRequest request);
    
}
