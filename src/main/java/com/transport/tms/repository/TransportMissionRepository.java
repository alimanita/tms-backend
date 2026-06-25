package com.transport.tms.repository;

import com.transport.tms.domain.entity.TransportMission;
import com.transport.tms.domain.enums.MissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransportMissionRepository extends JpaRepository<TransportMission, Long> {
    long countByStatusNot(MissionStatus status);

    @EntityGraph(attributePaths = {"customer", "customerOrder", "vehicle", "driver"})
    Page<TransportMission> findAllByOrderByDepartureDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "customerOrder", "vehicle", "driver"})
    Page<TransportMission> findAllByDriver_IdOrderByDepartureDateDesc(Long driverId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "customerOrder", "vehicle", "driver"})
    Optional<TransportMission> findWithDetailsById(Long id);

    boolean existsByReference(String reference);

    boolean existsByReferenceAndIdNot(String reference, Long id);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT m.vehicle.id FROM TransportMission m WHERE m.driver.id = :driverId AND m.vehicle IS NOT NULL")
    java.util.List<Long> findVehicleIdsByDriverId(Long driverId);
}
