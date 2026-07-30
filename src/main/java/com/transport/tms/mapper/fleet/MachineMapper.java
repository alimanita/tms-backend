package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.fleet.Machine;
import com.transport.tms.domain.enums.StatutMachine;
import com.transport.tms.dto.fleet.request.MachineRequest;
import com.transport.tms.dto.fleet.response.MachineResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MachineMapper {

    public Machine toEntity(MachineRequest request) {
        if (request == null) {
            return null;
        }

        Machine machine = new Machine();
        machine.setReference(request.reference());
        machine.setNumeroSerie(request.numeroSerie());
        machine.setNom(request.nom());
        machine.setMarque(request.marque());
        machine.setModele(request.modele());
        machine.setCategorie(request.categorie());
        
        machine.setDateAchat(request.dateAchat());
        machine.setPrixAchat(request.prixAchat());

        machine.setUnitesPuissance(request.unitesPuissance());
        machine.setValeurPuissance(request.valeurPuissance());
        machine.setHeuresInitiales(request.heuresInitiales() != null ? request.heuresInitiales() : BigDecimal.ZERO);
        machine.setHeuresActuelles(request.heuresActuelles() != null ? request.heuresActuelles() : BigDecimal.ZERO);
        machine.setLocalisation(request.localisation());
        
        machine.setStatut(request.statut() != null ? request.statut() : StatutMachine.DISPONIBLE);
        machine.setTauxDisponibilite(request.tauxDisponibilite() != null ? request.tauxDisponibilite() : BigDecimal.valueOf(100));

        machine.setNotes(request.notes());
        machine.setActif(request.actif() != null ? request.actif() : true);
        
        return machine;
    }

    public void updateEntity(Machine machine, MachineRequest request) {
        if (request == null) {
            return;
        }

        if (request.reference() != null) {
            machine.setReference(request.reference());
        }
        if (request.numeroSerie() != null) {
            machine.setNumeroSerie(request.numeroSerie());
        }
        if (request.nom() != null) {
            machine.setNom(request.nom());
        }
        if (request.marque() != null) {
            machine.setMarque(request.marque());
        }
        if (request.modele() != null) {
            machine.setModele(request.modele());
        }
        if (request.categorie() != null) {
            machine.setCategorie(request.categorie());
        }
        if (request.dateAchat() != null) {
            machine.setDateAchat(request.dateAchat());
        }
        if (request.prixAchat() != null) {
            machine.setPrixAchat(request.prixAchat());
        }
        if (request.unitesPuissance() != null) {
            machine.setUnitesPuissance(request.unitesPuissance());
        }
        if (request.valeurPuissance() != null) {
            machine.setValeurPuissance(request.valeurPuissance());
        }
        if (request.heuresInitiales() != null) {
            machine.setHeuresInitiales(request.heuresInitiales());
        }
        if (request.heuresActuelles() != null) {
            machine.setHeuresActuelles(request.heuresActuelles());
        }
        if (request.localisation() != null) {
            machine.setLocalisation(request.localisation());
        }
        if (request.statut() != null) {
            machine.setStatut(request.statut());
        }
        if (request.tauxDisponibilite() != null) {
            machine.setTauxDisponibilite(request.tauxDisponibilite());
        }
        if (request.notes() != null) {
            machine.setNotes(request.notes());
        }
        if (request.actif() != null) {
            machine.setActif(request.actif());
        }
    }

    public MachineResponse toResponse(Machine machine) {
        if (machine == null) {
            return null;
        }

        return new MachineResponse(
                machine.getId(),
                machine.getReference(),
                machine.getNumeroSerie(),
                machine.getNom(),
                machine.getMarque(),
                machine.getModele(),
                machine.getCategorie(),
                machine.getDateAchat(),
                machine.getPrixAchat(),
                machine.getUnitesPuissance(),
                machine.getValeurPuissance(),
                machine.getHeuresInitiales(),
                machine.getHeuresActuelles(),
                machine.getLocalisation(),
                machine.getStatut(),
                machine.getTauxDisponibilite(),
                machine.getNotes(),
                machine.getActif(),
                machine.getCreatedAt(),
                machine.getUpdatedAt()
        );
    }
}
