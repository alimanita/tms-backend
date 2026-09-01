package com.transport.tms.repository.fleet;

import com.transport.tms.domain.entity.fleet.SocietePartenaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocietePartenaireRepository extends JpaRepository<SocietePartenaire, Long> {
    Page<SocietePartenaire> findAll(Pageable pageable);
    List<SocietePartenaire> findByStatut(SocietePartenaire.StatutPartenaire statut);
}
