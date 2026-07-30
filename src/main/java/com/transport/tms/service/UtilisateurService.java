package com.transport.tms.service;

import com.transport.tms.dto.ChangerMotDePasseUtilisateurDto;
import com.transport.tms.dto.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {

  UtilisateurDto save(UtilisateurDto dto);

  UtilisateurDto createUtilisateur(UtilisateurDto dto, Long idEntreprise);

  List<UtilisateurDto> findByRoleAndEntreprise(String role, Long idEntreprise);

  UtilisateurDto findById(Long id);

  UtilisateurDto findByEmail(String email);

  List<UtilisateurDto> findAllByEntreprise(Long idEntreprise);

  List<UtilisateurDto> findAll();

  UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto);

  void delete(Long id);
}