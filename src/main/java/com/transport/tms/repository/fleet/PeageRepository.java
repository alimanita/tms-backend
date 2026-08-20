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
    List<Peage> findByMissionId(Long missionId);
    Page<Peage> findAll(Pageable pageable);

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
}
