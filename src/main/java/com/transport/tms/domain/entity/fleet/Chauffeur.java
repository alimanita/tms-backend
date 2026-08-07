package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.enums.StatutChauffeur;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fleet_chauffeur")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(unique = true, length = 30)
    private String cin;

    @Column(length = 20)
    private String telephone;

    @Column(length = 150)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    private LocalDate dateEmbauche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatutChauffeur statut = StatutChauffeur.DISPONIBLE;

    // ── Permis de conduire ─────────────────────────────────────

    @Column(length = 50)
    private String numeroPermis;

    @Column(length = 100)
    private String categoriesPermis;            // A, B, C, D, CE, ...

    private LocalDate dateDelivrancePermis;

    private LocalDate dateExpirationPermis;

    private LocalDate dateExpirationVisiteMedicale;

    // ── Statistiques ───────────────────────────────────────────

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalKilometres = BigDecimal.ZERO;

    @Builder.Default
    private Integer nombreIncidents = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private com.transport.tms.domain.enums.TypeSalaire typeSalaire;

    @Column(precision = 12, scale = 2)
    private BigDecimal valeurSalaire;

    @Builder.Default
    private Boolean actif = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", unique = true)
    private Utilisateur utilisateur;
    // ── Méthodes métier ────────────────────────────────────────

    public boolean isPermisValide() {
        return dateExpirationPermis != null
                && dateExpirationPermis.isAfter(LocalDate.now());
    }

    public String getFullName() {
        return prenom + " " + nom;
    }



    public boolean peutConduire(String categorieRequise) {
        return isPermisValide()
                && categoriesPermis != null
                && categoriesPermis.contains(categorieRequise);
    }


}