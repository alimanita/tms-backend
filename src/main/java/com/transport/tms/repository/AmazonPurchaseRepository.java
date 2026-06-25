package com.transport.tms.repository;

import com.transport.tms.domain.entity.AmazonPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmazonPurchaseRepository extends JpaRepository<AmazonPurchase, Long> {
    @EntityGraph(attributePaths = "items")
    Page<AmazonPurchase> findAllByOrderByPurchaseDateDesc(Pageable pageable);

    @EntityGraph(attributePaths = "items")
    Optional<AmazonPurchase> findWithItemsById(Long id);

    boolean existsByAmazonOrderNumber(String amazonOrderNumber);

    boolean existsByAmazonOrderNumberAndIdNot(String amazonOrderNumber, Long id);
}
