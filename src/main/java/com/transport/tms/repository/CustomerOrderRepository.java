package com.transport.tms.repository;

import com.transport.tms.domain.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    @EntityGraph(attributePaths = {"customer", "lines"})
    Page<CustomerOrder> findAllByOrderByOrderDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "lines"})
    Optional<CustomerOrder> findWithDetailsById(Long id);

    boolean existsByReference(String reference);

    boolean existsByReferenceAndIdNot(String reference, Long id);
}
