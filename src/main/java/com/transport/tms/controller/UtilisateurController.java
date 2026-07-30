package com.transport.tms.controller;



import com.transport.tms.controller.api.UtilisateurApi;
import com.transport.tms.dto.ChangerMotDePasseUtilisateurDto;
import com.transport.tms.dto.UtilisateurDto;
import com.transport.tms.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController

public class UtilisateurController implements UtilisateurApi {

  private UtilisateurService utilisateurService;


  @Autowired
  public UtilisateurController(UtilisateurService utilisateurService) {
    this.utilisateurService = utilisateurService;
  }

  @Override
  public UtilisateurDto save(UtilisateurDto dto) {
    return utilisateurService.save(dto);
  }
  @Override
  public List<UtilisateurDto> findByRole(String role, Long idEntreprise) {

    return utilisateurService.findByRoleAndEntreprise(role, idEntreprise);
  }
  @Override
  public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
    return utilisateurService.changerMotDePasse(dto);
  }

  @Override
  public UtilisateurDto findById(Long id) {
    return utilisateurService.findById(id);
  }

  @Override
  public UtilisateurDto findByEmail(String email) {
    return utilisateurService.findByEmail(email);
  }

  @Override
  public List<UtilisateurDto> findAll() {
    return utilisateurService.findAll();
  }

  @Override
  public void delete(Long id) {
    utilisateurService.delete(id);
  }
}
