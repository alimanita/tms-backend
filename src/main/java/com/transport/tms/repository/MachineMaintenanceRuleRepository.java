package com.transport.tms.repository;

import com.transport.tms.domain.entity.MachineMaintenanceRule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MachineMaintenanceRuleRepository extends JpaRepository<MachineMaintenanceRule, Long> {
    @EntityGraph(attributePaths = "machine")
    List<MachineMaintenanceRule> findAllByMachine_IdAndActiveTrueOrderByCodeAsc(Long machineId);

    @EntityGraph(attributePaths = "machine")
    Optional<MachineMaintenanceRule> findWithMachineById(Long id);
}
