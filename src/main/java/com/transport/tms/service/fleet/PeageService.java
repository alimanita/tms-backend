package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.response.PeageResponse;
import com.transport.tms.dto.fleet.response.OcrTollResult;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PeageService {
    Page<PeageResponse> findAll(Pageable pageable);
    PeageResponse findById(Long id);
    PeageResponse create(PeageRequest request, MultipartFile proof);
    void delete(Long id);
    Resource getProofFile(Long id);
    OcrTollResult extractTollData(MultipartFile proof);
    List<PeageResponse> findByVehicule(Long vehiculeId);
    List<PeageResponse> findByChauffeur(Long chauffeurId);
    List<PeageResponse> findByMission(Long missionId);
}
