package com.transport.tms.repository;

import com.transport.tms.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code);
    Set<Role> findByCodeIn(java.util.List<String> codes);
}
