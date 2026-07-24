package com.transport.tms.repository;

import com.transport.tms.domain.entity.OilChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, Long> {
    List<OilChange> findByVehicleId(Long vehicleId);
    Page<OilChange> findAllByOrderByChangeDateDesc(Pageable pageable);
}
