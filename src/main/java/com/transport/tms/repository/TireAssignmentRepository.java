package com.transport.tms.repository;

import com.transport.tms.domain.entity.TireAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TireAssignmentRepository extends JpaRepository<TireAssignment, Long> {
    List<TireAssignment> findByVehicleId(Long vehicleId);
    List<TireAssignment> findByTireId(Long tireId);
    Page<TireAssignment> findAllByOrderByMountDateDesc(Pageable pageable);
}
