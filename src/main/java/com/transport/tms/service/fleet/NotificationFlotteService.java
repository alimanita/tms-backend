package com.transport.tms.service.fleet;


import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationFlotteService {
    Page<NotificationFlotteResponse> findAll(Pageable pageable);
    List<NotificationFlotteResponse> findNonLues();
    List<NotificationFlotteResponse> findCritiques();
    NotificationFlotteResponse marquerLue(Long id);
    void marquerToutesLues();
    void ignorer(Long id);
    long countNonLues();
}