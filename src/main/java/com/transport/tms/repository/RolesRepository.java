package com.transport.tms.repository;




import com.transport.tms.domain.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {



    List<Roles> findByRoleNameIn(List<String> roleNames);
    Optional<Roles> findByRoleName(String roleName);
}