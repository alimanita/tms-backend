package com.transport.tms.dto;

import com.transport.tms.domain.entity.Adresse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    // Informations entreprise
    private String nomEntreprise;
    private String emailEntreprise;
    private String matriculeFiscal;
    private String codeFiscal;
    private String numTelEntreprise;
    private String siteWeb;
    private Adresse adresseEntreprise;

    // Informations admin (premier utilisateur)
    private String nomAdmin;
    private String prenomAdmin;
    private String emailAdmin;
    private String motDePasseAdmin;
    private Adresse adresseAdmin;

}