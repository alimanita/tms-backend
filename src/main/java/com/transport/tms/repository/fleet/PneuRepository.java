package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.Pneu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PneuRepository extends JpaRepository<Pneu, Long> {

    Optional<Pneu> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    Page<Pneu> findByIsActiveTrue(Pageable pageable);

    List<Pneu> findByStatusAndIsActiveTrue(Pneu.StatutPneu status);

    // Pneus disponibles en stock
    List<Pneu> findByStatusAndIsActiveTrueOrderByPurchaseDateAsc(Pneu.StatutPneu status);
}