package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.DepenseDiverseRequest;
import com.transport.tms.dto.fleet.response.DepenseDiverseResponse;
import com.transport.tms.dto.fleet.response.DepenseDiverseSummaryResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface DepenseDiverseService {

    Page<DepenseDiverseResponse> findAll(
            Long chauffeurId,
            String categorie,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    DepenseDiverseSummaryResponse getSummary(
            Long chauffeurId,
            String categorie,
            LocalDateTime startDate,
            LocalDateTime endDate);

    DepenseDiverseResponse findById(Long id);

    DepenseDiverseResponse create(DepenseDiverseRequest request, MultipartFile proof);

    DepenseDiverseResponse update(Long id, DepenseDiverseRequest request, MultipartFile proof);

    void delete(Long id);

    List<DepenseDiverseResponse> findByChauffeur(Long chauffeurId);

    Resource getProofFile(Long id);
}
