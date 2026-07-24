package com.transport.tms.repository;

import com.transport.tms.domain.entity.Tire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TireRepository extends JpaRepository<Tire, Long> {
    Page<Tire> findByActiveTrue(Pageable pageable);
    boolean existsBySerialNumberIgnoreCase(String serialNumber);
}
