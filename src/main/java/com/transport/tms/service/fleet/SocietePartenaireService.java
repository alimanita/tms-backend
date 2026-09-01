package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.SocietePartenaireRequest;
import com.transport.tms.dto.fleet.response.SocietePartenaireResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SocietePartenaireService {
    SocietePartenaireResponse create(SocietePartenaireRequest request);
    SocietePartenaireResponse update(Long id, SocietePartenaireRequest request);
    SocietePartenaireResponse findById(Long id);
    Page<SocietePartenaireResponse> findAll(Pageable pageable);
    List<SocietePartenaireResponse> findAllActive();
}
