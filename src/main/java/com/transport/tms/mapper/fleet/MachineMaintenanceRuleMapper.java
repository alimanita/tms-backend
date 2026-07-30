package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.Machine;
import com.transport.tms.domain.entity.fleet.MachineMaintenanceRule;
import com.transport.tms.dto.fleet.request.MachineMaintenanceRuleRequest;
import com.transport.tms.dto.fleet.response.MachineMaintenanceRuleResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MachineMaintenanceRuleMapper {

    public MachineMaintenanceRule toEntity(MachineMaintenanceRuleRequest request, Machine machine) {
        if (request == null) {
            return null;
        }

        return MachineMaintenanceRule.builder()
                .machine(machine)
                .code(request.code())
                .description(request.description())
                .typeAction(request.typeAction())
                .intervalleHeures(request.intervalleHeures())
                .intervalleJours(request.intervalleJours())
                .consommable(request.consommable())
                .quantite(request.quantite())
                .uniteQuantite(request.uniteQuantite())
                .dernieresHeuresEffectuees(request.dernieresHeuresEffectuees())
                .derniereDateEffectuee(request.derniereDateEffectuee())

                .build();
    }

    public void updateEntity(MachineMaintenanceRule entity, MachineMaintenanceRuleRequest request, Machine machine) {
        if (request == null) {
            return;
        }

        if (machine != null) {
            entity.setMachine(machine);
        }
        if (request.code() != null) {
            entity.setCode(request.code());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        if (request.typeAction() != null) {
            entity.setTypeAction(request.typeAction());
        }
        if (request.intervalleHeures() != null) {
            entity.setIntervalleHeures(request.intervalleHeures());
        }
        if (request.intervalleJours() != null) {
            entity.setIntervalleJours(request.intervalleJours());
        }
        if (request.consommable() != null) {
            entity.setConsommable(request.consommable());
        }
        if (request.quantite() != null) {
            entity.setQuantite(request.quantite());
        }
        if (request.uniteQuantite() != null) {
            entity.setUniteQuantite(request.uniteQuantite());
        }
        if (request.dernieresHeuresEffectuees() != null) {
            entity.setDernieresHeuresEffectuees(request.dernieresHeuresEffectuees());
        }
        if (request.derniereDateEffectuee() != null) {
            entity.setDerniereDateEffectuee(request.derniereDateEffectuee());
        }

    }

    public MachineMaintenanceRuleResponse toResponse(MachineMaintenanceRule entity) {
        if (entity == null) {
            return null;
        }

        Machine machine = entity.getMachine();

        BigDecimal heuresRestantes = null;
        Boolean prochaineEcheanceProche = null;

        if (machine != null && entity.getIntervalleHeures() != null && machine.getHeuresActuelles() != null) {
            BigDecimal heuresActuelles = machine.getHeuresActuelles();
            BigDecimal derniereEffectuee = entity.getDernieresHeuresEffectuees() != null
                    ? entity.getDernieresHeuresEffectuees() : BigDecimal.ZERO;
            BigDecimal intervalle = BigDecimal.valueOf(entity.getIntervalleHeures());

            BigDecimal heuresDepuisDerniere = heuresActuelles.subtract(derniereEffectuee);
            heuresRestantes = intervalle.subtract(heuresDepuisDerniere);
            prochaineEcheanceProche = heuresRestantes.compareTo(BigDecimal.valueOf(20)) <= 0;
        }

        return new MachineMaintenanceRuleResponse(
                entity.getId(),
                machine != null ? machine.getId() : null,
                machine != null ? machine.getReference() : null,
                machine != null ? machine.getNom() : null,
                entity.getCode(),
                entity.getDescription(),
                entity.getTypeAction(),
                entity.getIntervalleHeures(),
                entity.getIntervalleJours(),
                entity.getConsommable(),
                entity.getQuantite(),
                entity.getUniteQuantite(),
                entity.getDernieresHeuresEffectuees(),
                entity.getDerniereDateEffectuee(),
                prochaineEcheanceProche,
                heuresRestantes,
                entity.getActif(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}