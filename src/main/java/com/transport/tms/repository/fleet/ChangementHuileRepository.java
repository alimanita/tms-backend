package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.ChangementHuile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChangementHuileRepository extends JpaRepository<ChangementHuile, Long> {

    Optional<ChangementHuile> findByReference(String reference);

    boolean existsByReference(String reference);

    // Par entité
    List<ChangementHuile> findByEntityTypeAndEntityIdOrderByChangeDateDesc(
            ChangementHuile.TypeEntite entityType, Long entityId);

    Page<ChangementHuile> findByEntityTypeAndEntityId(
            ChangementHuile.TypeEntite entityType, Long entityId, Pageable pageable);

    // Dernier changement d'huile d'une entité
    Optional<ChangementHuile> findTopByEntityTypeAndEntityIdOrderByChangeDateDesc(
            ChangementHuile.TypeEntite entityType, Long entityId);

    // Vidanges à venir (date dépassée ou km proche)
    @Query("""
            SELECT c FROM ChangementHuile c
            WHERE c.nextChangeDate <= :limit
            OR c.nextChangeKm <= :kmActuel
            ORDER BY c.nextChangeDate ASC
            """)
    List<ChangementHuile> findAVenir(
            @Param("limit") LocalDate limit,
            @Param("kmActuel") BigDecimal kmActuel);

    // Vidanges dont le prochain km est atteint pour un véhicule
    @Query("""
            SELECT c FROM ChangementHuile c
            WHERE c.entityType = 'VEHICLE'
            AND c.entityId = :vehiculeId
            AND c.nextChangeKm IS NOT NULL
            AND c.nextChangeKm <= :kmActuel
            """)
    List<ChangementHuile> findVidangesKmAtteintes(
            @Param("vehiculeId") Long vehiculeId,
            @Param("kmActuel") BigDecimal kmActuel);
}