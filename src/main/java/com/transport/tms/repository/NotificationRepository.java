package com.transport.tms.repository;

import com.transport.tms.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByReadFlagFalse();

    List<Notification> findByReadFlagFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
