package com.transport.tms.service.fleet;

import com.transport.tms.dto.fleet.request.DepenseMissionRequest;
import com.transport.tms.dto.fleet.request.MissionRequest;
import com.transport.tms.dto.fleet.response.DepenseMissionResponse;
import com.transport.tms.dto.fleet.response.MissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MissionService {
    MissionResponse create(MissionRequest request, org.springframework.web.multipart.MultipartFile letter);
    MissionResponse update(Long id, MissionRequest request, org.springframework.web.multipart.MultipartFile letter);
    MissionResponse uploadLetter(Long id, org.springframework.web.multipart.MultipartFile file);
    org.springframework.core.io.Resource getLetterMission(Long id);
    MissionResponse findById(Long id);
    Page<MissionResponse> findAll(Pageable pageable);
    List<MissionResponse> findByVehicule(Long vehiculeId);
    List<MissionResponse> findByChauffeur(Long chauffeurId);
    List<MissionResponse> findEnCours();
    List<MissionResponse> findEnAttenteApprobation();
   // MissionResponse soumettre(Long id);
    //MissionResponse approuver(Long id);
    //MissionResponse rejeter(Long id, String motif);
    MissionResponse demarrer(Long id, java.math.BigDecimal mileageAtDeparture);
    MissionResponse cloturer(Long id, java.math.BigDecimal mileageAtReturn);
    MissionResponse annuler(Long id, String motif);
    DepenseMissionResponse addDepense(Long id, DepenseMissionRequest request, org.springframework.web.multipart.MultipartFile receipt);
    List<DepenseMissionResponse> findDepenses(Long id);
    void removeDepense(Long id, Long depenseId);
    org.springframework.core.io.Resource getDepenseReceipt(Long id, Long depenseId);
    List<MissionResponse> findMesMissions();
    Page<DepenseMissionResponse> findAllTolls(Pageable pageable);
}