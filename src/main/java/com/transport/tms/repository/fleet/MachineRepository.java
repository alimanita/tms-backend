package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.Machine;
import com.transport.tms.domain.enums.StatutMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Optional<Machine> findByReference(String reference);

    boolean existsByReference(String reference);

    Page<Machine> findByActifTrue(Pageable pageable);

    List<Machine> findByStatutAndActifTrue(StatutMachine statut);

    // Machines disponibles
    List<Machine> findByStatutAndActifTrueOrderByNomAsc(StatutMachine statut);

    // Par localisation
    @Query("""
            SELECT m FROM Machine m
            WHERE m.actif = true
            AND LOWER(m.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))
            ORDER BY m.nom ASC
            """)
    List<Machine> findByLocalisation(@Param("localisation") String localisation);

    // Par catégorie
    List<Machine> findByCategorieAndActifTrue(String categorie);

    // Résolution nom pour entityRef dans OrdreTravailServiceImpl
    @Query("""
            SELECT CONCAT(m.reference, ' — ', m.nom)
            FROM Machine m
            WHERE m.id = :id
            """)
    Optional<String> findRefLabelById(@Param("id") Long id);
    long countByIdInAndStatutAndActifTrue(List<Long> ids, StatutMachine statut);

    // Comptage global (sans filtre technicien) — pour stats mécanicien
    long countByActifTrue();
    long countByStatutAndActifTrue(StatutMachine statut);
}