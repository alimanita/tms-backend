package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
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



    private Utilisateur utilisateurConnecte(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof Utilisateur u) {
            return u;
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
        // Assuming there is a findByActifTrue method or similar, 
        // falling back to filtering all if not present in the repository interface
        return chauffeurRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActif()))
                .map(chauffeurMapper::toResponse)
                .collect(Collectors.toList());
    }
}
