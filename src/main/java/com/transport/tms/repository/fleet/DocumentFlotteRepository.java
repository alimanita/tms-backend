package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.domain.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentFlotteRepository extends JpaRepository<DocumentFlotte, Long> {

    // Par entité
    List<DocumentFlotte> findByEntityTypeAndEntityId(
            DocumentFlotte.TypeEntite entityType, Long entityId);

    List<DocumentFlotte> findByEntityTypeAndEntityIdAndStatus(
            DocumentFlotte.TypeEntite entityType,
            Long entityId,
            DocumentFlotte.StatutDocument status);

    // Par type de document
    List<DocumentFlotte> findByTypeDocumentAndEntityTypeAndEntityId(
            DocumentType typeDocument,
            DocumentFlotte.TypeEntite entityType,
            Long entityId);

    // Documents expirant avant une date
    @Query("""
            SELECT d FROM DocumentFlotte d
            WHERE d.status = 'ACTIVE'
            AND d.expiryDate IS NOT NULL
            AND d.expiryDate <= :limit
            ORDER BY d.expiryDate ASC
            """)
    List<DocumentFlotte> findExpirantAvant(@Param("limit") LocalDate limit);

    // Documents déjà expirés
    @Query("""
            SELECT d FROM DocumentFlotte d
            WHERE d.status = 'ACTIVE'
            AND d.expiryDate < :today
            """)
    List<DocumentFlotte> findExpires(@Param("today") LocalDate today);

    // Documents expirant entre deux dates
    @Query("""
            SELECT d FROM DocumentFlotte d
            WHERE d.status = 'ACTIVE'
            AND d.expiryDate BETWEEN :debut AND :fin
            ORDER BY d.expiryDate ASC
            """)
    List<DocumentFlotte> findExpirantEntre(
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    Page<DocumentFlotte> findByStatus(DocumentFlotte.StatutDocument status, Pageable pageable);



}