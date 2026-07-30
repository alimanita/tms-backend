package com.transport.tms.mapper.fleet;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.enums.StatutChauffeur;
import com.transport.tms.dto.fleet.request.ChauffeurRequest;
import com.transport.tms.dto.fleet.response.ChauffeurResponse;

import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ChauffeurMapper {

    private final UtilisateurRepository utilisateurRepository;

    public Chauffeur toEntity(ChauffeurRequest request) {
        if (request == null) {
            return null;
        }

        Chauffeur chauffeur = new Chauffeur();

        chauffeur.setNom(request.nom());
        chauffeur.setPrenom(request.prenom());
        chauffeur.setCin(request.cin());
        chauffeur.setTelephone(request.telephone());
        chauffeur.setEmail(request.email());
        chauffeur.setAdresse(request.adresse());
        chauffeur.setDateEmbauche(request.dateEmbauche());

        chauffeur.setStatut(request.statut() != null ? request.statut() : StatutChauffeur.DISPONIBLE);

        chauffeur.setNumeroPermis(request.numeroPermis());
        chauffeur.setCategoriesPermis(request.categoriesPermis());
        chauffeur.setDateDelivrancePermis(request.dateDelivrancePermis());
        chauffeur.setDateExpirationPermis(request.dateExpirationPermis());
        chauffeur.setDateExpirationVisiteMedicale(request.dateExpirationVisiteMedicale());

        chauffeur.setTotalKilometres(request.totalKilometres() != null ? request.totalKilometres() : BigDecimal.ZERO);
        chauffeur.setNombreIncidents(request.nombreIncidents() != null ? request.nombreIncidents() : 0);

        chauffeur.setNotes(request.notes());
        chauffeur.setActif(request.actif() != null ? request.actif() : true);

        resolveUtilisateur(request.idUtilisateur(), chauffeur);

        return chauffeur;
    }

    public void updateEntity(Chauffeur chauffeur, ChauffeurRequest request) {
        if (request == null) {
            return;
        }

        if (request.nom() != null) {
            chauffeur.setNom(request.nom());
        }
        if (request.prenom() != null) {
            chauffeur.setPrenom(request.prenom());
        }
        if (request.cin() != null) {
            chauffeur.setCin(request.cin());
        }
        if (request.telephone() != null) {
            chauffeur.setTelephone(request.telephone());
        }
        if (request.email() != null) {
            chauffeur.setEmail(request.email());
        }
        if (request.adresse() != null) {
            chauffeur.setAdresse(request.adresse());
        }
        if (request.dateEmbauche() != null) {
            chauffeur.setDateEmbauche(request.dateEmbauche());
        }
        if (request.statut() != null) {
            chauffeur.setStatut(request.statut());
        }
        if (request.numeroPermis() != null) {
            chauffeur.setNumeroPermis(request.numeroPermis());
        }
        if (request.categoriesPermis() != null) {
            chauffeur.setCategoriesPermis(request.categoriesPermis());
        }
        if (request.dateDelivrancePermis() != null) {
            chauffeur.setDateDelivrancePermis(request.dateDelivrancePermis());
        }
        if (request.dateExpirationPermis() != null) {
            chauffeur.setDateExpirationPermis(request.dateExpirationPermis());
        }
        if (request.dateExpirationVisiteMedicale() != null) {
            chauffeur.setDateExpirationVisiteMedicale(request.dateExpirationVisiteMedicale());
        }
        if (request.totalKilometres() != null) {
            chauffeur.setTotalKilometres(request.totalKilometres());
        }
        if (request.nombreIncidents() != null) {
            chauffeur.setNombreIncidents(request.nombreIncidents());
        }
        if (request.notes() != null) {
            chauffeur.setNotes(request.notes());
        }
        if (request.actif() != null) {
            chauffeur.setActif(request.actif());
        }

        // idUtilisateur : traité explicitement même si null, pour permettre le déliaison
        resolveUtilisateur(request.idUtilisateur(), chauffeur);
    }

    public ChauffeurResponse toResponse(Chauffeur chauffeur) {
        if (chauffeur == null) {
            return null;
        }

        Utilisateur utilisateur = chauffeur.getUtilisateur();

        return new ChauffeurResponse(
                chauffeur.getId(),
                chauffeur.getNom(),
                chauffeur.getPrenom(),
                chauffeur.getCin(),
                chauffeur.getTelephone(),
                chauffeur.getEmail(),
                chauffeur.getAdresse(),
                chauffeur.getDateEmbauche(),
                chauffeur.getStatut(),
                chauffeur.getNumeroPermis(),
                chauffeur.getCategoriesPermis(),
                chauffeur.getDateDelivrancePermis(),
                chauffeur.getDateExpirationPermis(),
                chauffeur.getDateExpirationVisiteMedicale(),
                chauffeur.getTotalKilometres(),
                chauffeur.getNombreIncidents(),
                chauffeur.getNotes(),
                chauffeur.getActif(),
                chauffeur.getCreatedAt(),
                chauffeur.getUpdatedAt(),
                utilisateur != null ? utilisateur.getId() : null,
                utilisateur != null ? utilisateur.getEmail() : null
        );
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private void resolveUtilisateur(Long idUtilisateur, Chauffeur chauffeur) {
        if (idUtilisateur == null) {
            chauffeur.setUtilisateur(null);
            return;
        }
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
        chauffeur.setUtilisateur(utilisateur);
    }
}