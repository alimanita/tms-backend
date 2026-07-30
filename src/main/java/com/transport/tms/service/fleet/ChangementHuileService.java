package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.ChangementHuileRequest;
import com.transport.tms.dto.fleet.response.ChangementHuileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChangementHuileService {

    Page<ChangementHuileResponse> findAll(Pageable pageable);

    ChangementHuileResponse findById(Long id);

    ChangementHuileResponse create(ChangementHuileRequest request);

    ChangementHuileResponse update(Long id, ChangementHuileRequest request);

    void delete(Long id);

    List<ChangementHuileResponse> findByVehicule(Long vehiculeId);

    List<ChangementHuileResponse> findByMachine(Long machineId);

    List<ChangementHuileResponse> findAVenir();
}