package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.Peage;
import com.transport.tms.domain.entity.fleet.Vehicule;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.Mission;
import com.transport.tms.dto.fleet.request.PeageRequest;
import com.transport.tms.dto.fleet.response.OcrTollResult;
import com.transport.tms.dto.fleet.response.PeageResponse;
import com.transport.tms.mapper.fleet.PeageMapper;
import com.transport.tms.repository.fleet.PeageRepository;
import com.transport.tms.repository.fleet.VehiculeRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.MissionRepository;
import com.transport.tms.service.fleet.FileStorageService;
import com.transport.tms.service.fleet.PeageService;
import com.transport.tms.service.fleet.ReceiptOcrService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeageServiceImpl implements PeageService {

    private final PeageRepository peageRepository;
    private final PeageMapper peageMapper;
    private final VehiculeRepository vehiculeRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final MissionRepository missionRepository;
    private final FileStorageService fileStorageService;
    private final ReceiptOcrService receiptOcrService;

    @Override
    @Transactional(readOnly = true)
    public Page<PeageResponse> findAll(Pageable pageable) {
        return peageRepository.findAll(pageable).map(peageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PeageResponse findById(Long id) {
        return peageMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional
    public PeageResponse create(PeageRequest request, MultipartFile proof) {
        Vehicule vehicule = vehiculeRepository.findById(request.vehiculeId())
                .orElseThrow(() -> new EntityNotFoundException("Véhicule non trouvé"));

        Peage peage = peageMapper.toEntity(request);
        peage.setReference(genererReference());
        peage.setVehicule(vehicule);

        if (request.chauffeurId() != null) {
            Chauffeur chauffeur = chauffeurRepository.findById(request.chauffeurId())
                    .orElseThrow(() -> new EntityNotFoundException("Chauffeur non trouvé"));
            peage.setChauffeur(chauffeur);
        }

        if (request.missionId() != null) {
            Mission mission = missionRepository.findById(request.missionId())
                    .orElseThrow(() -> new EntityNotFoundException("Mission non trouvée"));
            peage.setMission(mission);
        }

        if (proof != null && !proof.isEmpty()) {
            String filename = fileStorageService.store(proof, "");
            peage.setProofFilePath(filename);
        }

        peage = peageRepository.save(peage);
        return peageMapper.toResponse(peage);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Peage peage = findEntityById(id);
        if (peage.getProofFilePath() != null) {
            fileStorageService.delete(peage.getProofFilePath(), "");
        }
        peageRepository.delete(peage);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getProofFile(Long id) {
        Peage peage = findEntityById(id);
        if (peage.getProofFilePath() == null) {
            throw new EntityNotFoundException("Aucun justificatif pour ce péage");
        }
        return fileStorageService.load(peage.getProofFilePath());
    }

    @Override
    public OcrTollResult extractTollData(MultipartFile proof) {
        return receiptOcrService.extractTollData(proof);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeageResponse> findByVehicule(Long vehiculeId) {
        return peageRepository.findByVehiculeId(vehiculeId).stream()
                .map(peageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeageResponse> findByChauffeur(Long chauffeurId) {
        return peageRepository.findByChauffeurId(chauffeurId).stream()
                .map(peageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeageResponse> findByMission(Long missionId) {
        return peageRepository.findByMissionId(missionId).stream()
                .map(peageMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Peage findEntityById(Long id) {
        return peageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Péage non trouvé avec l'id : " + id));
    }

    private String genererReference() {
        return "PG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
