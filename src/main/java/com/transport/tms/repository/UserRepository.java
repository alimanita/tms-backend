package com.transport.tms.repository;

import com.transport.tms.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndActiveTrue(String username);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
