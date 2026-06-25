package com.transport.tms.repository;

import com.transport.tms.domain.entity.FuelRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuelRecordRepository extends JpaRepository<FuelRecord, Long> {
    @EntityGraph(attributePaths = {"vehicle", "driver"})
    Page<FuelRecord> findAllByOrderByFillDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"vehicle", "driver"})
    Page<FuelRecord> findAllByDriver_IdOrderByFillDateDesc(Long driverId, Pageable pageable);

    @EntityGraph(attributePaths = {"vehicle", "driver"})
    Optional<FuelRecord> findWithDetailsById(Long id);
}
