package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.Peage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeageRepository extends JpaRepository<Peage, Long> {
    List<Peage> findByVehiculeId(Long vehiculeId);
    List<Peage> findByChauffeurId(Long chauffeurId);
    Page<Peage> findByChauffeurId(Long chauffeurId, Pageable pageable);
    List<Peage> findByMissionId(Long missionId);
    Page<Peage> findAll(Pageable pageable);

    boolean existsByReceiptNumber(String receiptNumber);
    boolean existsByReceiptNumberAndIdNot(String receiptNumber, Long id);

    @org.springframework.data.jpa.repository.Query("""
        SELECT DISTINCT p FROM Peage p
        LEFT JOIN FETCH p.vehicule v
        LEFT JOIN FETCH p.chauffeur c
        WHERE p.mission IS NULL
        AND p.datePassage BETWEEN :debut AND :fin
        AND (:filterVehicule = false OR v.id IN :vehiculeIds)
        AND (:filterChauffeur = false OR c.id IN :chauffeurIds)
    """)
    List<Peage> findStandaloneForBilanExploitation(
            @org.springframework.data.repository.query.Param("debut") java.time.LocalDateTime debut,
            @org.springframework.data.repository.query.Param("fin") java.time.LocalDateTime fin,
            @org.springframework.data.repository.query.Param("vehiculeIds") List<Long> vehiculeIds,
            @org.springframework.data.repository.query.Param("filterVehicule") boolean filterVehicule,
            @org.springframework.data.repository.query.Param("chauffeurIds") List<Long> chauffeurIds,
            @org.springframework.data.repository.query.Param("filterChauffeur") boolean filterChauffeur);

    @org.springframework.data.jpa.repository.Query("""
            SELECT COALESCE(SUM(p.amountTTC), 0)
            FROM Peage p
            """)
    java.math.BigDecimal sumAllCoutPeage();

    @org.springframework.data.jpa.repository.Query("""
        SELECT EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage), COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.datePassage >= :fromDate
        GROUP BY EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage)
        ORDER BY EXTRACT(YEAR FROM p.datePassage), EXTRACT(MONTH FROM p.datePassage)
        """)
    List<Object[]> sumCostByYearMonth(@org.springframework.data.repository.query.Param("fromDate") java.time.LocalDateTime fromDate);

    @org.springframework.data.jpa.repository.Query("""
        SELECT COALESCE(SUM(p.amountTTC), 0)
        FROM Peage p
        WHERE p.mission.id = :missionId
    """)
    java.math.BigDecimal sumPeageByMissionId(@org.springframework.data.repository.query.Param("missionId") Long missionId);
}
