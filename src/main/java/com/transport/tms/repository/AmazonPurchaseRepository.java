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

    // --- Aggregations pour les Rapports ---

    @org.springframework.data.jpa.repository.Query("SELECT a.supplier, SUM(a.amountTtc) FROM AmazonPurchase a GROUP BY a.supplier")
    java.util.List<Object[]> sumExpensesBySupplier();

    @org.springframework.data.jpa.repository.Query("SELECT EXTRACT(MONTH FROM a.purchaseDate), SUM(a.amountTtc) FROM AmazonPurchase a GROUP BY EXTRACT(MONTH FROM a.purchaseDate)")
    java.util.List<Object[]> sumExpensesByMonth();

    /** Total de tous les achats Amazon (pour le KPI dashboard). */
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(a.amountTtc), 0) FROM AmazonPurchase a")
    java.math.BigDecimal sumAllAmountTtc();

    /** Achats Amazon mensuels (6 derniers mois) pour le graphique dashboard. */
    @org.springframework.data.jpa.repository.Query("SELECT EXTRACT(YEAR FROM a.purchaseDate), EXTRACT(MONTH FROM a.purchaseDate), COALESCE(SUM(a.amountTtc), 0) FROM AmazonPurchase a WHERE a.purchaseDate >= :fromDate GROUP BY EXTRACT(YEAR FROM a.purchaseDate), EXTRACT(MONTH FROM a.purchaseDate) ORDER BY EXTRACT(YEAR FROM a.purchaseDate), EXTRACT(MONTH FROM a.purchaseDate)")
    java.util.List<Object[]> sumExpensesByYearMonth(@org.springframework.data.repository.query.Param("fromDate") java.time.LocalDate fromDate);
}