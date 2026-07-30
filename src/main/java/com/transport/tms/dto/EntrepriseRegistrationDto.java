package com.transport.tms.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntrepriseRegistrationDto {

    private String nom;
    private String description;
    private AdresseDto adresse;
    private String codeFiscal;
    private String email;
    private String numTel;
    private String steWeb;
    private String matriculeFiscal;
    private UtilisateurAdminDto utilisateurAdmin;

}