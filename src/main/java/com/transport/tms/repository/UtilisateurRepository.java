package com.transport.tms.repository;

import com.transport.tms.domain.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

  Optional<Utilisateur> findByEmail(String email);

  Optional<Utilisateur> findByUsername(String username);

  Optional<Utilisateur> findByEmailOrUsername(String email, String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  List<Utilisateur> findAllByEntrepriseId(Long idEntreprise);

  @Query("""
        SELECT DISTINCT u FROM Utilisateur u
        JOIN u.roles r
        WHERE (r.roleName = :role
        OR r.roleName = CONCAT('ROLE_', :role)
        OR r.roleName = SUBSTRING(:role, 6))
        AND u.entreprise.id = :idEntreprise
        """)
  List<Utilisateur> findByRoleAndEntreprise(@Param("role") String role, @Param("idEntreprise") Long idEntreprise);

  @Query("""
        SELECT DISTINCT u FROM Utilisateur u
        JOIN u.roles r
        WHERE (r.roleName = :role
        OR r.roleName = CONCAT('ROLE_', :role)
        OR r.roleName = SUBSTRING(:role, 6))
        """)
  List<Utilisateur> findByRole(@Param("role") String role);

  @Query("""
            SELECT DISTINCT u.id FROM Utilisateur u
            JOIN u.roles r
            WHERE r.roleName IN :roleNames
            """)
  List<Long> findIdsByRoleIn(@Param("roleNames") List<String> roleNames);
}