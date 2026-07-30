package com.transport.tms.controller;


import com.transport.tms.controller.api.EntrepriseApi;
import com.transport.tms.dto.EntrepriseDto;
import com.transport.tms.dto.EntrepriseRegistrationDto;
import com.transport.tms.service.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class EntrepriseController implements EntrepriseApi {

  private EntrepriseService entrepriseService;

  @Autowired
  public EntrepriseController(EntrepriseService entrepriseService) {
    this.entrepriseService = entrepriseService;
  }

  @Override
  public EntrepriseDto register(EntrepriseRegistrationDto dto) {
    // Endpoint public pour l'inscription (pas de @PreAuthorize)
    return entrepriseService.register(dto);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
  public EntrepriseDto save(EntrepriseDto dto) {
    return entrepriseService.save(dto);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
  public EntrepriseDto findById(Long id) {
    return entrepriseService.findById(id);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
  public List<EntrepriseDto> findAll() {
    return entrepriseService.findAll();
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
  public void delete(Long id) {
    entrepriseService.delete(id);
  }
}