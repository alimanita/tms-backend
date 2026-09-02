package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutVehicule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByReference(String reference);
    boolean existsByReference(String reference);
    boolean existsByImmatriculation(String immatriculation);
    Page<Vehicule> findByActifTrue(Pageable pageable);
    List<Vehicule> findByStatutAndActifTrue(StatutVehicule statut);
    List<Vehicule> findByStatutAndActifTrueOrderByReferenceAsc(StatutVehicule statut);
    long countByActifTrue();
    Page<Vehicule> findByChauffeurAffecteId(Long chauffeurId, Pageable pageable);

    @Query("""
            SELECT CONCAT(v.reference, ' — ', v.marque, ' ', v.modele)
            FROM Vehicule v WHERE v.id = :id
            """)
    Optional<String> findRefLabelById(@Param("id") Long id);


    long countByChauffeurAffecteId(Long chauffeurId);

    long countByChauffeurAffecteIdAndStatut(Long chauffeurId, StatutVehicule statut);
    @Query("SELECT v.reference FROM Vehicule v WHERE v.reference LIKE CONCAT('VH-', :annee, '-%') ORDER BY v.reference DESC LIMIT 1")
    Optional<String> findLastReferenceForYear(@Param("annee") int annee);
    @Query("SELECT v.id FROM Vehicule v WHERE v.chauffeurAffecte.id = :chauffeurId")
    List<Long> findIdsByChauffeurAffecteId(@Param("chauffeurId") Long chauffeurId);
}