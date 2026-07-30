package com.transport.tms.validator;

import com.transport.tms.dto.UtilisateurDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurValidator {

  public static List<String> validate(UtilisateurDto utilisateurDto) {
    List<String> errors = new ArrayList<>();

    if (utilisateurDto == null) {
      errors.add("Veuillez renseigner le nom d'utilisateur");
      errors.add("Veuillez renseigner l'email d'utilisateur");
      errors.add("Veuillez renseigner le mot de passe d'utilisateur");
      return errors;
    }

    if (!StringUtils.hasLength(utilisateurDto.getUsername())) {
      errors.add("Veuillez renseigner le nom d'utilisateur");
    }
    if (!StringUtils.hasLength(utilisateurDto.getFullName())) {
      errors.add("Veuillez renseigner le nom complet d'utilisateur");
    }
    if (!StringUtils.hasLength(utilisateurDto.getEmail())) {
      errors.add("Veuillez renseigner l'email d'utilisateur");
    }
    if (!StringUtils.hasLength(utilisateurDto.getPassword())) {
      errors.add("Veuillez renseigner le mot de passe d'utilisateur");
    }

    return errors;
  }

}