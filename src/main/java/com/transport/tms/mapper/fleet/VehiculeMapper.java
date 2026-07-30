package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.enums.StatutVehicule;
import com.transport.tms.dto.fleet.request.VehiculeRequest;
import com.transport.tms.dto.fleet.response.VehiculeResponse;
import org.springframework.stereotype.Component;

@Component
public class VehiculeMapper {

    public Vehicule toEntity(VehiculeRequest request) {
        Vehicule vehicule = new Vehicule();
        vehicule.setReference(request.reference());
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setAnnee(request.annee());
        vehicule.setTypeCarburant(request.typeCarburant());
        vehicule.setKilometrageActuel(request.kilometrageActuel());
        vehicule.setCapaciteReservoir(request.capaciteReservoir());
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setActif(true);
        vehicule.setIdEntreprise(request.idEntreprise());
        return vehicule;
    }

    public void updateEntity(Vehicule vehicule, VehiculeRequest request) {
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setAnnee(request.annee());
        vehicule.setTypeCarburant(request.typeCarburant());
        vehicule.setCapaciteReservoir(request.capaciteReservoir());
    }

    public VehiculeResponse toResponse(Vehicule vehicule) {
        String chauffeurNom = null;
        if (vehicule.getChauffeurAffecte() != null) {
            chauffeurNom = vehicule.getChauffeurAffecte().getNom()
                    + " " + vehicule.getChauffeurAffecte().getPrenom();
        }

        return new VehiculeResponse(
                vehicule.getId(),
                vehicule.getReference(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getAnnee(),
                vehicule.getTypeCarburant(),
                vehicule.getStatut(),
                vehicule.getKilometrageActuel(),
                vehicule.getCapaciteReservoir(),
                vehicule.getChauffeurAffecte() != null ? vehicule.getChauffeurAffecte().getId() : null,
                chauffeurNom,
                vehicule.getActif(),
                vehicule.getCreatedAt(),
                vehicule.getUpdatedAt()
        );
    }
}