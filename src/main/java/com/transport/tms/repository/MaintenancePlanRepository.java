package com.transport.tms.repository;

import com.transport.tms.domain.entity.MaintenancePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long> {
    Page<MaintenancePlan> findByActiveTrue(Pageable pageable);
    List<MaintenancePlan> findByVehicleId(Long vehicleId);
}
