package com.transport.tms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.transport.tms.domain.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolesDto {

  private Long id;
  private String roleName;


  @JsonIgnore
  private Set<Integer> utilisateurIds; // Juste les IDs pour éviter les boucles infinies

  public static RolesDto fromEntity(Roles roles) {
    if (roles == null) {
      return null;
    }
    return RolesDto.builder()
            .id(roles.getId())
            .roleName(roles.getRoleName())
            // Ne pas mapper les utilisateurs ici pour éviter les références circulaires
            .build();
  }

  public static Roles toEntity(RolesDto dto) {
    if (dto == null) {
      return null;
    }
    Roles roles = new Roles();
    roles.setId(dto.getId());
    roles.setRoleName(dto.getRoleName());
    // Ne pas mapper les utilisateurs ici, cela sera géré par UtilisateurService
    return roles;
  }
}