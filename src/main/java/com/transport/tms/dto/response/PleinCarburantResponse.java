package com.transport.tms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PleinCarburantResponse {

    private Long id;
    private Long vehiculeId;
    private String vehiculeImmatriculation;
    private Long chauffeurId;
    private String chauffeurNom;
    private LocalDate datePlein;
    private Double quantiteLitres;
    private Double montantTotal;
    private Integer kilometrage;
    private String station;
    private String proofUrl;
}