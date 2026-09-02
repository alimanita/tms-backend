package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.DocumentFlotte;
import com.transport.tms.dto.fleet.request.DocumentFlotteRequest;
import com.transport.tms.dto.fleet.response.DocumentFlotteResponse;
import com.transport.tms.mapper.fleet.DocumentFlotteMapper;
import com.transport.tms.repository.fleet.DocumentFlotteRepository;
import com.transport.tms.service.fleet.DocumentFlotteService;
import com.transport.tms.service.fleet.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DocumentFlotteServiceImpl implements DocumentFlotteService {
    private final DocumentFlotteRepository documentRepository;
    private final DocumentFlotteMapper mapper;
    private final FileStorageService fileStorageService;

    @Override
    public DocumentFlotteResponse create(DocumentFlotteRequest request, org.springframework.web.multipart.MultipartFile file) {
        DocumentFlotte doc = mapper.toEntity(request);
        if (file != null && !file.isEmpty()) {
            String filename = fileStorageService.store(file);
            doc.setFilePath(filename);
            doc.setFileName(file.getOriginalFilename());
        }
        return mapper.toResponse(documentRepository.save(doc),
                resolveEntityRef(request.entityType(), request.entityId()));
    }

    @Override
    public DocumentFlotteResponse update(Long id, DocumentFlotteRequest request, org.springframework.web.multipart.MultipartFile file) {
        DocumentFlotte doc = findEntityById(id);
        mapper.updateEntity(doc, request);
        if (file != null && !file.isEmpty()) {
            if (doc.getFilePath() != null) fileStorageService.delete(doc.getFilePath());
            String filename = fileStorageService.store(file);
            doc.setFilePath(filename);
            doc.setFileName(file.getOriginalFilename());
        }
        return mapper.toResponse(documentRepository.save(doc),
                resolveEntityRef(doc.getEntityType(), doc.getEntityId()));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFlotteResponse findById(Long id) {
        DocumentFlotte doc = findEntityById(id);
        return mapper.toResponse(doc, resolveEntityRef(doc.getEntityType(), doc.getEntityId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentFlotteResponse> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable)
                .map(d -> mapper.toResponse(d,
                        resolveEntityRef(d.getEntityType(), d.getEntityId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFlotteResponse> findByVehicule(Long vehiculeId) {
        return documentRepository
                .findByEntityTypeAndEntityId(DocumentFlotte.TypeEntite.VEHICLE, vehiculeId)
                .stream()
                .map(d -> mapper.toResponse(d, resolveEntityRef(d.getEntityType(), d.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFlotteResponse> findByChauffeur(Long chauffeurId) {
        return documentRepository
                .findByEntityTypeAndEntityId(DocumentFlotte.TypeEntite.DRIVER, chauffeurId)
                .stream()
                .map(d -> mapper.toResponse(d, resolveEntityRef(d.getEntityType(), d.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFlotteResponse> findByMachine(Long machineId) {
        return documentRepository
                .findByEntityTypeAndEntityId(DocumentFlotte.TypeEntite.MACHINE, machineId)
                .stream()
                .map(d -> mapper.toResponse(d, resolveEntityRef(d.getEntityType(), d.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFlotteResponse> findExpirantBientot(int jours) {
        return documentRepository.findExpirantAvant(LocalDate.now().plusDays(jours))
                .stream()
                .map(d -> mapper.toResponse(d, resolveEntityRef(d.getEntityType(), d.getEntityId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentFlotteResponse> findExpires() {
        return documentRepository.findExpires(LocalDate.now())
                .stream()
                .map(d -> mapper.toResponse(d, resolveEntityRef(d.getEntityType(), d.getEntityId())))
                .toList();
    }

    @Override
    public void delete(Long id) {
        DocumentFlotte doc = findEntityById(id);
        doc.setStatus(DocumentFlotte.StatutDocument.CANCELLED);
        documentRepository.save(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.core.io.Resource getFile(Long id) {
        DocumentFlotte doc = findEntityById(id);
        if (doc.getFilePath() == null) {
            throw new EntityNotFoundException("Aucun fichier associé au document ID = " + id);
        }
        return fileStorageService.load(doc.getFilePath());
    }

    // ── Méthodes internes ─────────────────────────────────────

    private DocumentFlotte findEntityById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Document introuvable avec l'ID = " + id));
    }

    private String resolveEntityRef(DocumentFlotte.TypeEntite type, Long entityId) {
        if (type == null) return "N/A";
        if (entityId == null) return type.name();
        return type.name() + " #" + entityId;
    }
}