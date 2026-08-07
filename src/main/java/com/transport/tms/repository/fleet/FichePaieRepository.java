package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.FichePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FichePaieRepository extends JpaRepository<FichePaie, Long> {
    List<FichePaie> findByChauffeurIdOrderByMoisAnneeDesc(Long chauffeurId);
    Optional<FichePaie> findByChauffeurIdAndMoisAnnee(Long chauffeurId, String moisAnnee);
}
