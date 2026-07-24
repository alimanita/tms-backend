package com.transport.tms.repository;

import com.transport.tms.domain.entity.PieceRechange;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PieceRechangeRepository extends JpaRepository<PieceRechange, Long> {

    Optional<PieceRechange> findByReference(String reference);

    boolean existsByReference(String reference);

    Page<PieceRechange> findByIsActiveTrue(Pageable pageable);

    // Pièces sous le seuil minimum
    @Query("""
            SELECT p FROM PieceRechange p
            WHERE p.isActive = true
            AND p.stockQty <= p.minStockQty
            ORDER BY p.stockQty ASC
            """)
    List<PieceRechange> findStockFaible();

    // Pièces en rupture

}