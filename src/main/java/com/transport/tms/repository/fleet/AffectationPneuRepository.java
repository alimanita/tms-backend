package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.AffectationPneu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationPneuRepository extends JpaRepository<AffectationPneu, Long> {

    // Pneus actuellement montés sur un véhicule
    List<AffectationPneu> findByVehiculeIdAndUnmountDateIsNull(Long vehiculeId);

    // Historique complet d'un véhicule
    List<AffectationPneu> findByVehiculeIdOrderByMountDateDesc(Long vehiculeId);

    // Historique d'un pneu
    List<AffectationPneu> findByPneuIdOrderByMountDateDesc(Long pneuId);

    // Vérifier si un pneu est déjà monté
    boolean existsByPneuIdAndUnmountDateIsNull(Long pneuId);

    // Affectation active d'un pneu
    Optional<AffectationPneu> findByPneuIdAndUnmountDateIsNull(Long pneuId);

    // Vérifier si une position est déjà occupée sur un véhicule
    @Query("""
            SELECT COUNT(a) > 0 FROM AffectationPneu a
            WHERE a.vehicule.id = :vehiculeId
            AND a.position = :position
            AND a.unmountDate IS NULL
            """)
    boolean existsPositionOccupee(
            @Param("vehiculeId") Long vehiculeId,
            @Param("position") AffectationPneu.PositionPneu position);
}