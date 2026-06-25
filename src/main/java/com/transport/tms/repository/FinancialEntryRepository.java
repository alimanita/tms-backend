package com.transport.tms.repository;

import com.transport.tms.domain.entity.FinancialEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialEntryRepository extends JpaRepository<FinancialEntry, Long> {
    Page<FinancialEntry> findAllByOrderByEntryDateDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FinancialEntry f WHERE f.entryType = :entryType")
    BigDecimal sumAmountByEntryType(String entryType);

    @Query("""
            SELECT f.entryDate, COALESCE(SUM(f.amount), 0)
            FROM FinancialEntry f
            WHERE f.entryType = :entryType AND f.entryDate >= :fromDate
            GROUP BY f.entryDate
            ORDER BY f.entryDate
            """)
    List<Object[]> sumAmountGroupedByDate(String entryType, LocalDate fromDate);
}
