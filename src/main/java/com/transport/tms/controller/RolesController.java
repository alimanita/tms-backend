package com.transport.tms.controller;

import com.transport.tms.controller.api.RolesApi;
import com.transport.tms.dto.RolesDto;
import com.transport.tms.service.RolesService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j

@RestController
public class RolesController implements RolesApi {

    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Override
    public RolesDto save(RolesDto dto) {
        log.info("Création rôle : {}", dto.getRoleName());
        return rolesService.save(dto);
    }

    @Override
    public RolesDto findById(Long idRole) {
        return rolesService.findById(idRole);
    }

    @Override
    public List<RolesDto> findAll() {
        return rolesService.findAll();
    }

    @Override
    public void delete(Long idRole) {
        log.info("Suppression rôle ID : {}", idRole);
        rolesService.delete(idRole);
    }
}