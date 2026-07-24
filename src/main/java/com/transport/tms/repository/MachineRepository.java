package com.transport.tms.repository;

import com.transport.tms.domain.entity.Machine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Page<Machine> findByActiveTrueOrderByReferenceAsc(Pageable pageable);
    List<Machine> findByActiveTrueOrderByReferenceAsc();
    Optional<Machine> findByIdAndActiveTrue(Long id);
    boolean existsByReferenceIgnoreCase(String reference);
    boolean existsByReferenceIgnoreCaseAndIdNot(String reference, Long id);
}
