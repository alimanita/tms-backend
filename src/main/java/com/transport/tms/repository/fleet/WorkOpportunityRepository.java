package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.WorkOpportunity;
import com.transport.tms.domain.enums.StatutOffre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkOpportunityRepository extends JpaRepository<WorkOpportunity, Long>,
        JpaSpecificationExecutor<WorkOpportunity> {

    // Find all DISPONIBLE ordered by pickup time
    Page<WorkOpportunity> findByStatutOrderByFirstPickupTimeAsc(StatutOffre statut, Pageable pageable);

    // Nearby search using Haversine formula (native query with subquery for PostgreSQL)
    @Query(value = """
      SELECT sub.id, sub.empty_km FROM (
        SELECT id, (
          6371 * acos(
            LEAST(1.0, GREATEST(-1.0,
              cos(radians(:lat)) * cos(radians(departure_lat)) *
              cos(radians(departure_lng) - radians(:lng)) +
              sin(radians(:lat)) * sin(radians(departure_lat))
            ))
          )
        ) AS empty_km
        FROM work_opportunity
        WHERE statut = 'DISPONIBLE'
          AND departure_lat IS NOT NULL
          AND departure_lng IS NOT NULL
          AND (:afterDate IS NULL OR first_pickup_time >= :afterDate\\:\\:timestamp)
          AND (:minRevenue IS NULL OR payout_value >= :minRevenue)
      ) sub
      WHERE sub.empty_km <= :radiusKm
      ORDER BY sub.empty_km ASC
      LIMIT :limitCount
      """, nativeQuery = true)
    List<Object[]> findNearbyDepartures(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm,
            @Param("afterDate") String afterDate,
            @Param("minRevenue") BigDecimal minRevenue,
            @Param("limitCount") int limitCount
    );

    // Find by externalId
    java.util.Optional<WorkOpportunity> findByExternalId(String externalId);

    // Check for existing by externalId (unique identifier)
    boolean existsByExternalId(String externalId);

    // Check for existing by externalId + source (for dedup)
    boolean existsByExternalIdAndSource(String externalId, String source);

    // Check for existing course by business key (departure, arrival, pickup date, payout)
    boolean existsByDepartureCityIgnoreCaseAndArrivalCityIgnoreCaseAndFirstPickupTimeAndPayoutValue(
            String departureCity, String arrivalCity, LocalDateTime firstPickupTime, BigDecimal payoutValue
    );

    // Delete expired
    @Modifying
    @Query("DELETE FROM WorkOpportunity w WHERE w.lastDeliveryTime < :now AND w.statut = 'DISPONIBLE'")
    int deleteExpired(@Param("now") LocalDateTime now);
}
