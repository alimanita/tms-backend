package com.transport.tms.repository;

import com.transport.tms.domain.entity.MaintenanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    @EntityGraph(attributePaths = "vehicle")
    Page<MaintenanceRecord> findAllByOrderByMaintenanceDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = "vehicle")
    Page<MaintenanceRecord> findAllByVehicle_IdInOrderByMaintenanceDateDesc(java.util.List<Long> vehicleIds, Pageable pageable);

    @EntityGraph(attributePaths = "vehicle")
    Optional<MaintenanceRecord> findWithDetailsById(Long id);
}
