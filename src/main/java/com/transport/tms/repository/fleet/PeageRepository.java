package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.Peage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeageRepository extends JpaRepository<Peage, Long> {
    List<Peage> findByVehiculeId(Long vehiculeId);
    List<Peage> findByChauffeurId(Long chauffeurId);
    List<Peage> findByMissionId(Long missionId);
    Page<Peage> findAll(Pageable pageable);
}
