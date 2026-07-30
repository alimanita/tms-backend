package com.transport.tms.validator;


import com.transport.tms.dto.EntrepriseDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EntrepriseValidator {

  public static List<String> validate(EntrepriseDto dto) {
    List<String> errors = new ArrayList<>();

    if (dto == null) {
      errors.add("Veuillez renseigner les informations de l'entreprise");
      return errors;
    }

    if (!StringUtils.hasLength(dto.getNom())) {
      errors.add("Veuillez renseigner le nom de l'entreprise");
    }

    if (!StringUtils.hasLength(dto.getEmail())) {
      errors.add("Veuillez renseigner l'email de l'entreprise");
    }

    if (!StringUtils.hasLength(dto.getMatriculeFiscal())) {
      errors.add("Veuillez renseigner le matricule fiscal");
    }

    return errors;
  }

}