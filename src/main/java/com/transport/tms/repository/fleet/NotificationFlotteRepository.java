package com.transport.tms.repository.fleet;


import com.transport.tms.domain.entity.fleet.NotificationFlotte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationFlotteRepository extends JpaRepository<NotificationFlotte, Long> {

    // Non lues et non ignorées
    List<NotificationFlotte> findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc();

    Page<NotificationFlotte> findByIsDismissedFalseOrderByCreatedAtDesc(Pageable pageable);

    // Par sévérité
    List<NotificationFlotte> findBySeverityAndIsReadFalseAndIsDismissedFalse(
            NotificationFlotte.Severite severity);

    // Par type
    List<NotificationFlotte> findByTypeAndIsDismissedFalse(
            NotificationFlotte.TypeNotification type);

    // Par entité
    List<NotificationFlotte> findByEntityTypeAndEntityIdAndIsDismissedFalse(
            NotificationFlotte.TypeEntite entityType, Long entityId);

    // Comptage non lues
    long countByIsReadFalseAndIsDismissedFalse();

    // Comptage critiques non lues
    long countBySeverityAndIsReadFalseAndIsDismissedFalse(NotificationFlotte.Severite severity);

    // Marquer toutes comme lues
    @Modifying
    @Query("""
            UPDATE NotificationFlotte n
            SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP
            WHERE n.isRead = false
            """)
    int marquerToutesLues();

    // Vérifier doublon (éviter de créer la même notification deux fois le même jour)
    @Query("""
            SELECT COUNT(n) > 0 FROM NotificationFlotte n
            WHERE n.type = :type
            AND n.entityType = :entityType
            AND n.entityId = :entityId
            AND CAST(n.createdAt AS date) = CURRENT_DATE
            """)
    boolean existsAujourdhui(
            @Param("type") NotificationFlotte.TypeNotification type,
            @Param("entityType") NotificationFlotte.TypeEntite entityType,
            @Param("entityId") Long entityId);
}