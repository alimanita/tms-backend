package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.enums.StatutChauffeur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {


    Optional<Chauffeur> findByUtilisateurId(Long idUtilisateur);

    boolean existsByCin(String cin);

    Page<Chauffeur> findByActifTrue(Pageable pageable);

    List<Chauffeur> findByStatutAndActifTrue(StatutChauffeur statut);

    // Chauffeurs disponibles avec permis valide
    @Query("""
            SELECT c FROM Chauffeur c
            WHERE c.statut = 'DISPONIBLE'
            AND c.actif = true
            AND (c.dateExpirationPermis IS NULL OR c.dateExpirationPermis > :today)
            ORDER BY c.nom ASC
            """)
    List<Chauffeur> findDisponibles(@Param("today") LocalDate today);

    // Permis expirant dans X jours
    @Query("""
            SELECT c FROM Chauffeur c
            WHERE c.actif = true
            AND c.dateExpirationPermis BETWEEN :today AND :limit
            ORDER BY c.dateExpirationPermis ASC
            """)
    List<Chauffeur> findPermisExpirantAvant(
            @Param("today") LocalDate today,
            @Param("limit") LocalDate limit);

    // Permis déjà expirés
    @Query("""
            SELECT c FROM Chauffeur c
            WHERE c.actif = true
            AND c.dateExpirationPermis < :today
            """)
    List<Chauffeur> findPermisExpires(@Param("today") LocalDate today);

    // Visites médicales expirant dans X jours
    @Query("""
            SELECT c FROM Chauffeur c
            WHERE c.actif = true
            AND c.dateExpirationVisiteMedicale BETWEEN :today AND :limit
            ORDER BY c.dateExpirationVisiteMedicale ASC
            """)
    List<Chauffeur> findVisitesMedicalesExpirantAvant(
            @Param("today") LocalDate today,
            @Param("limit") LocalDate limit);

    // Visites médicales déjà expirées
    @Query("""
            SELECT c FROM Chauffeur c
            WHERE c.actif = true
            AND c.dateExpirationVisiteMedicale < :today
            """)
    List<Chauffeur> findVisitesMedicalesExpirees(@Param("today") LocalDate today);

}