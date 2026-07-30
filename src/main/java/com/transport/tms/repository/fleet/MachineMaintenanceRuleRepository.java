package com.transport.tms.repository.fleet;



import com.transport.tms.domain.entity.fleet.MachineMaintenanceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MachineMaintenanceRuleRepository extends JpaRepository<MachineMaintenanceRule, Long> {

    List<MachineMaintenanceRule> findByMachineIdAndActifTrue(Long machineId);

    List<MachineMaintenanceRule> findByMachineId(Long machineId);

    @Query("SELECT r FROM MachineMaintenanceRule r WHERE r.actif = true")
    List<MachineMaintenanceRule> findAllActives();

    void deleteByMachineId(Long machineId);
    List<MachineMaintenanceRule> findByActifTrueAndMachine_ActifTrue();

    long countByActifTrueAndMachineIdIn(List<Long> machineIds);

    // Comptage global pour stats mécanicien
    long countByActifTrue();
}