package com.transport.tms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.transport.tms.domain.entity.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDto {

  private Long id;
  private String username;
  private String fullName;
  private String email;
  private String phone;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Ne pas exposer le password en lecture
  private String password;

  private boolean active;
  private Long driverId;
  private Instant createdAt;

  private EntrepriseDto entreprise;
  private Set<RolesDto> roles;

  public static UtilisateurDto fromEntity(Utilisateur utilisateur) {
    if (utilisateur == null) {
      return null;
    }

    return UtilisateurDto.builder()
            .id(utilisateur.getId())
            .username(utilisateur.getUsername())
            .fullName(utilisateur.getFullName())
            .email(utilisateur.getEmail())
            .phone(utilisateur.getPhone())
            .password(utilisateur.getPassword()) // Sera masqué par @JsonProperty
            .active(utilisateur.isActive())
            .driverId(utilisateur.getDriverId())
            .createdAt(utilisateur.getCreatedAt())
            .entreprise(EntrepriseDto.fromEntity(utilisateur.getEntreprise()))
            .roles(
                    utilisateur.getRoles() != null ?
                            utilisateur.getRoles().stream()
                                    .map(RolesDto::fromEntity)
                                    .collect(Collectors.toSet()) : new HashSet<>()
            )
            .build();
  }

  public static Utilisateur toEntity(UtilisateurDto dto) {
    if (dto == null) {
      return null;
    }

    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setId(dto.getId());
    utilisateur.setUsername(dto.getUsername());
    utilisateur.setFullName(dto.getFullName());
    utilisateur.setEmail(dto.getEmail());
    utilisateur.setPhone(dto.getPhone());
    utilisateur.setPassword(dto.getPassword());
    utilisateur.setActive(dto.isActive());
    utilisateur.setDriverId(dto.getDriverId());
    utilisateur.setEntreprise(EntrepriseDto.toEntity(dto.getEntreprise()));
    // Note : roles non re-mappés ici (choix identique à ton code original)

    return utilisateur;
  }
}