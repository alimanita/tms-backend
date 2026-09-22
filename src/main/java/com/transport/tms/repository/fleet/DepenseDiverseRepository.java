package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.DepenseDiverse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepenseDiverseRepository extends JpaRepository<DepenseDiverse, Long>,
        JpaSpecificationExecutor<DepenseDiverse> {

    List<DepenseDiverse> findByChauffeurId(Long chauffeurId);

    Page<DepenseDiverse> findByChauffeurId(Long chauffeurId, Pageable pageable);

    List<DepenseDiverse> findByVehiculeId(Long vehiculeId);

    @Query("""
        SELECT COALESCE(SUM(d.amountTTC), 0)
        FROM DepenseDiverse d
    """)
    java.math.BigDecimal sumAllCout();

    @Query("""
        SELECT EXTRACT(YEAR FROM d.dateDepense), EXTRACT(MONTH FROM d.dateDepense), COALESCE(SUM(d.amountTTC), 0)
        FROM DepenseDiverse d
        WHERE d.dateDepense >= :fromDate
        GROUP BY EXTRACT(YEAR FROM d.dateDepense), EXTRACT(MONTH FROM d.dateDepense)
        ORDER BY EXTRACT(YEAR FROM d.dateDepense), EXTRACT(MONTH FROM d.dateDepense)
    """)
    List<Object[]> sumCostByYearMonth(@Param("fromDate") java.time.LocalDateTime fromDate);

    @Query("""
        SELECT DISTINCT d FROM DepenseDiverse d
        LEFT JOIN FETCH d.vehicule v
        LEFT JOIN FETCH d.chauffeur c
        WHERE d.dateDepense BETWEEN :debut AND :fin
        AND (:filterVehicule = false OR v.id IN :vehiculeIds)
        AND (:filterChauffeur = false OR c.id IN :chauffeurIds)
    """)
    List<DepenseDiverse> findForBilanExploitation(
            @Param("debut") java.time.LocalDateTime debut,
            @Param("fin") java.time.LocalDateTime fin,
            @Param("vehiculeIds") List<Long> vehiculeIds,
            @Param("filterVehicule") boolean filterVehicule,
            @Param("chauffeurIds") List<Long> chauffeurIds,
            @Param("filterChauffeur") boolean filterChauffeur);
}
