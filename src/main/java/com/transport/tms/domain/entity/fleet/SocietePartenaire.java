package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "societes_partenaires")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class SocietePartenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(name = "matricule_fiscal", length = 50)
    private String matriculeFiscal;

    @Column(length = 300)
    private String adresse;

    @Column(length = 100)
    private String contact;

    @Column(length = 30)
    private String telephone;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    private String iban;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatutPartenaire statut = StatutPartenaire.ACTIF;

    @Column(name = "taux_commission_defaut", precision = 5, scale = 2)
    private BigDecimal tauxCommissionDefaut;

    public enum StatutPartenaire {
        ACTIF, INACTIF, EN_ATTENTE
    }
}
