package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.request.DocumentFlotteRequest;
import com.transport.tms.dto.fleet.response.DocumentFlotteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentFlotteService {
    DocumentFlotteResponse create(DocumentFlotteRequest request, org.springframework.web.multipart.MultipartFile file);
    DocumentFlotteResponse update(Long id, DocumentFlotteRequest request, org.springframework.web.multipart.MultipartFile file);
    DocumentFlotteResponse findById(Long id);
    Page<DocumentFlotteResponse> findAll(Pageable pageable);
    List<DocumentFlotteResponse> findByVehicule(Long vehiculeId);
    List<DocumentFlotteResponse> findByChauffeur(Long chauffeurId);
    List<DocumentFlotteResponse> findByMachine(Long machineId);
    List<DocumentFlotteResponse> findExpirantBientot(int jours);
    List<DocumentFlotteResponse> findExpires();
    org.springframework.core.io.Resource getFile(Long id);
    void delete(Long id);
}