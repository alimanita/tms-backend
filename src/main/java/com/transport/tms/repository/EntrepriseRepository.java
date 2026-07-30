package com.transport.tms.repository;


import com.transport.tms.domain.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    Optional<Entreprise> findByEmail(String email);


    @Query("SELECT e FROM Entreprise e WHERE e.active = true ORDER BY e.id ASC")
    Optional<Entreprise> findFirstActive();

    Optional<Entreprise> findByMatriculeFiscal(String matriculeFiscal);
    boolean existsByEmail(String email);
    List<Entreprise> findByCodeGroupe(String codeGroupe);
    List<Entreprise> findByCodeGroupeIsNotNull();
    boolean existsByMatriculeFiscal(String matriculeFiscal);
}
