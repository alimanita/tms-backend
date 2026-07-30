package com.transport.tms.service;


import com.transport.tms.dto.EntrepriseDto;
import com.transport.tms.dto.EntrepriseRegistrationDto;

import java.util.List;

public interface EntrepriseService {

  
  EntrepriseDto register(EntrepriseRegistrationDto dto);
  EntrepriseDto save(EntrepriseDto dto);

  EntrepriseDto findById(Long id);

  EntrepriseDto findByEmail(String email);

  EntrepriseDto findByMatriculeFiscal(String matriculeFiscal);

  List<EntrepriseDto> findAll();

  void delete(Long id);
}
