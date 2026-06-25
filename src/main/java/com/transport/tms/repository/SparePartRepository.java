package com.transport.tms.repository;

import com.transport.tms.domain.entity.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {
    Page<SparePart> findByActiveTrue(Pageable pageable);

    boolean existsByReferenceIgnoreCase(String reference);

    boolean existsByReferenceIgnoreCaseAndIdNot(String reference, Long id);
}
