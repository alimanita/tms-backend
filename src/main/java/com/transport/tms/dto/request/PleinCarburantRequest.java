package com.transport.tms.dto.request;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PleinCarburantRequest {

    @NotNull
    private Long vehiculeId;

    private Long chauffeurId;

    @NotNull
    @PastOrPresent
    private LocalDate datePlein;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double quantiteLitres;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double montantTotal;

    private Integer kilometrage;

    private String station;
}