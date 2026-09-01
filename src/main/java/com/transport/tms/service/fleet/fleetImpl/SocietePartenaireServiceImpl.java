package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.SocietePartenaire;
import com.transport.tms.dto.fleet.request.SocietePartenaireRequest;
import com.transport.tms.dto.fleet.response.SocietePartenaireResponse;
import jakarta.persistence.EntityNotFoundException;
import com.transport.tms.repository.fleet.SocietePartenaireRepository;
import com.transport.tms.service.fleet.SocietePartenaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SocietePartenaireServiceImpl implements SocietePartenaireService {

    private final SocietePartenaireRepository repository;

    private SocietePartenaireResponse toResponse(SocietePartenaire p) {
        return new SocietePartenaireResponse(
                p.getId(),
                p.getNom(),
                p.getMatriculeFiscal(),
                p.getAdresse(),
                p.getContact(),
                p.getTelephone(),
                p.getEmail(),
                p.getIban(),
                p.getStatut().name(),
                p.getTauxCommissionDefaut()
        );
    }

    @Override
    public SocietePartenaireResponse create(SocietePartenaireRequest request) {
        SocietePartenaire partenaire = new SocietePartenaire();
        partenaire.setNom(request.nom());
        partenaire.setMatriculeFiscal(request.matriculeFiscal());
        partenaire.setAdresse(request.adresse());
        partenaire.setContact(request.contact());
        partenaire.setTelephone(request.telephone());
        partenaire.setEmail(request.email());
        partenaire.setIban(request.iban());
        if (request.statut() != null) {
            partenaire.setStatut(SocietePartenaire.StatutPartenaire.valueOf(request.statut()));
        }
        partenaire.setTauxCommissionDefaut(request.tauxCommissionDefaut());
        return toResponse(repository.save(partenaire));
    }

    @Override
    public SocietePartenaireResponse update(Long id, SocietePartenaireRequest request) {
        SocietePartenaire partenaire = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable"));
        
        partenaire.setNom(request.nom());
        partenaire.setMatriculeFiscal(request.matriculeFiscal());
        partenaire.setAdresse(request.adresse());
        partenaire.setContact(request.contact());
        partenaire.setTelephone(request.telephone());
        partenaire.setEmail(request.email());
        partenaire.setIban(request.iban());
        if (request.statut() != null) {
            partenaire.setStatut(SocietePartenaire.StatutPartenaire.valueOf(request.statut()));
        }
        partenaire.setTauxCommissionDefaut(request.tauxCommissionDefaut());
        
        return toResponse(repository.save(partenaire));
    }

    @Override
    @Transactional(readOnly = true)
    public SocietePartenaireResponse findById(Long id) {
        SocietePartenaire p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partenaire introuvable"));
        return toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SocietePartenaireResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocietePartenaireResponse> findAllActive() {
        return repository.findByStatut(SocietePartenaire.StatutPartenaire.ACTIF)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
