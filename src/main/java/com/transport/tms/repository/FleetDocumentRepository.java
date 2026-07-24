package com.transport.tms.repository;

import com.transport.tms.domain.entity.FleetDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FleetDocumentRepository extends JpaRepository<FleetDocument, Long> {
    List<FleetDocument> findByVehicleId(Long vehicleId);
    List<FleetDocument> findByDriverId(Long driverId);
    Page<FleetDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
