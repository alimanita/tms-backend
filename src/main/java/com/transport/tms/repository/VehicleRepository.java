package com.transport.tms.repository;

import com.transport.tms.domain.entity.Vehicle;
import com.transport.tms.domain.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findByActiveTrue(Pageable pageable);

    long countByActiveTrueAndStatus(VehicleStatus status);

    boolean existsByRegistrationIgnoreCase(String registration);
}
