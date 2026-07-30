package com.transport.tms.controller;

import com.transport.tms.controller.api.RoleStatisticsApi;
import com.transport.tms.dto.statistique.RoleDashboardStatsDto;
import com.transport.tms.service.RoleStatisticsService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class RoleStatisticsController implements RoleStatisticsApi {

    private final RoleStatisticsService roleStatisticsService;

    @Autowired
    public RoleStatisticsController(RoleStatisticsService roleStatisticsService) {
        this.roleStatisticsService = roleStatisticsService;
    }

    @Override
    public RoleDashboardStatsDto getRoleDashboardStats(
            @RequestParam(required = false) Integer idEntreprise
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;

        log.info("GET /statistics/role-dashboard - idEntreprise: {}, user: {}", idEntreprise, username);
        return roleStatisticsService.computeStats(idEntreprise, username);
    }
}