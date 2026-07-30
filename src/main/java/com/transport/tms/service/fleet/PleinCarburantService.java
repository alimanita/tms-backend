package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.PleinCarburantRequest;
import com.transport.tms.dto.fleet.response.PleinCarburantResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PleinCarburantService {
    PleinCarburantResponse create(PleinCarburantRequest request, MultipartFile proof);
    PleinCarburantResponse findById(Long id);
    Page<PleinCarburantResponse> findAll(Pageable pageable);
    List<PleinCarburantResponse> findByVehicule(Long vehiculeId);
    List<PleinCarburantResponse> findByChauffeur(Long chauffeurId);
    void delete(Long id);
    Resource getProofFile(Long id);
}