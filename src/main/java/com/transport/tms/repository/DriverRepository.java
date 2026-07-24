package com.transport.tms.repository;

import com.transport.tms.domain.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Page<Driver> findByActiveTrue(Pageable pageable);

    long countByActiveTrue();

    java.util.List<Driver> findByStatutAndActiveTrue(String statut);

    java.util.Optional<Driver> findByIdAndActiveTrue(Long id);
}
