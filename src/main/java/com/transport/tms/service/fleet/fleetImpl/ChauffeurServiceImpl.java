package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.dto.fleet.request.ChauffeurConfigRequest;
import com.transport.tms.dto.fleet.request.ChauffeurRequest;
import com.transport.tms.dto.fleet.response.ChauffeurResponse;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.mapper.fleet.ChauffeurMapper;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.service.fleet.ChauffeurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChauffeurServiceImpl implements ChauffeurService {

    private final ChauffeurRepository chauffeurRepository;
    private final ChauffeurMapper chauffeurMapper;
    private final ChauffeurMapper mapper;


    private final com.transport.tms.repository.UtilisateurRepository utilisateurRepository;

    private Utilisateur utilisateurConnecte(Authentication auth) {
        if (auth != null) {
            Object principal = auth.getPrincipal();
            String username = null;
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                username = userDetails.getUsername();
            } else if (principal instanceof String str) {
                username = str;
            }
            if (username != null) {
                return utilisateurRepository.findByEmail(username)
                        .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé en base"));
            }
        }
        throw new AccessDeniedException("Utilisateur non authentifié");
    }

    @Override
    public ChauffeurResponse create(ChauffeurRequest request) {
        log.info("Création d'un nouveau chauffeur: {}", request.nom());
        Chauffeur chauffeur = chauffeurMapper.toEntity(request);
        Chauffeur savedChauffeur = chauffeurRepository.save(chauffeur);
        return chauffeurMapper.toResponse(savedChauffeur);
    }

    @Override
    public ChauffeurResponse update(Long id, ChauffeurRequest request) {
        log.info("Mise à jour du chauffeur ID: {}", id);
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                )); // Assuming ErrorCodes has this or similar

        chauffeurMapper.updateEntity(chauffeur, request);
        Chauffeur savedChauffeur = chauffeurRepository.save(chauffeur);
        return chauffeurMapper.toResponse(savedChauffeur);
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression (soft) du chauffeur ID: {}", id);
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        chauffeur.setActif(false);
        chauffeurRepository.save(chauffeur);
    }
    @Override
    @Transactional(readOnly = true)
    public ChauffeurResponse findMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utilisateur utilisateur = utilisateurConnecte(auth); // même pattern que MissionServiceImpl
        Chauffeur chauffeur = chauffeurRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aucun profil chauffeur lié à cet utilisateur"));
        return mapper.toResponse(chauffeur);
    }
    @Override
    @Transactional(readOnly = true)
    public ChauffeurResponse getById(Long id) {
        log.info("Recherche du chauffeur ID: {}", id);
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
        return chauffeurMapper.toResponse(chauffeur);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChauffeurResponse> getAll(Pageable pageable) {
        log.info("Récupération paginée de tous les chauffeurs");
        return chauffeurRepository.findAll(pageable)
                .map(chauffeurMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChauffeurResponse> getAllActive() {
        log.info("Récupération de tous les chauffeurs actifs");
        return chauffeurRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActif()))
                .map(chauffeurMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChauffeurResponse toggleActif(Long id) {
        log.info("Toggle actif du chauffeur ID: {}", id);
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chauffeur introuvable, id=" + id));

        chauffeur.setActif(!Boolean.TRUE.equals(chauffeur.getActif()));
        Chauffeur saved = chauffeurRepository.save(chauffeur);
        return chauffeurMapper.toResponse(saved);
    }

    @Override
    public List<ChauffeurResponse> updateSettings(ChauffeurConfigRequest request) {
        log.info("Mise à jour des paramètres de visibilité des chauffeurs, global={}", request.isGlobal());

        List<Chauffeur> chauffeurs;
        if (Boolean.TRUE.equals(request.isGlobal())) {
            chauffeurs = chauffeurRepository.findAll();
        } else {
            if (request.chauffeurIds() == null || request.chauffeurIds().isEmpty()) {
                throw new IllegalArgumentException("La liste des IDs de chauffeurs ne peut pas être vide pour un paramétrage personnalisé.");
            }
            chauffeurs = chauffeurRepository.findAllById(request.chauffeurIds());
        }

        for (Chauffeur c : chauffeurs) {
            if (request.showTarif() != null)    c.setShowTarif(request.showTarif());
            if (request.showCout() != null)      c.setShowCout(request.showCout());
            if (request.showCarburant() != null) c.setShowCarburant(request.showCarburant());
        }

        return chauffeurRepository.saveAll(chauffeurs).stream()
                .map(chauffeurMapper::toResponse)
                .collect(Collectors.toList());
    }
}
