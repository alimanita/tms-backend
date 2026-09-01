package com.transport.tms.controller.api.fleet;

import com.transport.tms.dto.fleet.request.MissionClotureRequest;
import jakarta.validation.Valid;
import com.transport.tms.dto.fleet.request.DepenseMissionRequest;
import com.transport.tms.dto.fleet.request.MissionRequest;
import com.transport.tms.dto.fleet.response.DepenseMissionResponse;
import com.transport.tms.dto.fleet.response.MissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/fleet/missions")
public interface MissionApi {

    @GetMapping
    ResponseEntity<Page<MissionResponse>> findAll(Pageable pageable);

    @GetMapping("/{id}")
    ResponseEntity<MissionResponse> findById(@PathVariable Long id);

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MissionResponse> create(
        @RequestPart("mission") @Valid MissionRequest request,
        @RequestPart(value = "letter", required = false) org.springframework.web.multipart.MultipartFile letter);

    @PutMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<MissionResponse> update(@PathVariable Long id,
                                           @RequestPart("mission") @Valid MissionRequest request,
                                           @RequestPart(value = "letter", required = false) org.springframework.web.multipart.MultipartFile letter);

    @GetMapping("/{id}/letter")
    ResponseEntity<org.springframework.core.io.Resource> downloadLetter(@PathVariable Long id);

/*    @PatchMapping("/{id}/soumettre")
    ResponseEntity<MissionResponse> soumettre(@PathVariable Long id);

    @PatchMapping("/{id}/approuver")
    ResponseEntity<MissionResponse> approuver(@PathVariable Long id);

    @PatchMapping("/{id}/rejeter")
    ResponseEntity<MissionResponse> rejeter(@PathVariable Long id,
                                          @RequestParam String motif);

 */

    @PatchMapping("/{id}/demarrer")
    ResponseEntity<MissionResponse> demarrer(@PathVariable Long id,
                                             @RequestParam(required = false) java.math.BigDecimal mileageAtDeparture);

    @PatchMapping("/{id}/cloturer")
    ResponseEntity<MissionResponse> cloturer(@PathVariable Long id,
                                             @RequestParam(required = false) java.math.BigDecimal mileageAtReturn);

    @PatchMapping("/{id}/annuler")
    ResponseEntity<MissionResponse> annuler(@PathVariable Long id,
                                            @RequestParam String motif);

    @PostMapping(value = "/{id}/depenses", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<DepenseMissionResponse> addDepense(@PathVariable Long id,
                                                      @RequestPart("depense") @Valid DepenseMissionRequest request,
                                                      @RequestPart(value = "receipt", required = false) org.springframework.web.multipart.MultipartFile receipt);

    @GetMapping("/{id}/depenses")
    ResponseEntity<List<DepenseMissionResponse>> findDepenses(@PathVariable Long id);

    @DeleteMapping("/{id}/depenses/{depenseId}")
    ResponseEntity<Void> removeDepense(@PathVariable Long id, @PathVariable Long depenseId);

    @GetMapping("/{id}/depenses/{depenseId}/receipt")
    ResponseEntity<org.springframework.core.io.Resource> downloadDepenseReceipt(@PathVariable Long id, @PathVariable Long depenseId);

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<MissionResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<MissionResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/en-cours")
    ResponseEntity<List<MissionResponse>> findEnCours();
    @GetMapping("/mes-missions")
    ResponseEntity<List<MissionResponse>> findMesMissions();

    @GetMapping("/depenses/tolls")
    ResponseEntity<Page<DepenseMissionResponse>> findAllTolls(Pageable pageable);

    @PostMapping(value = "/extract-ai", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<com.transport.tms.dto.fleet.response.OcrMissionResult> extractMissionData(
            @RequestPart("file") org.springframework.web.multipart.MultipartFile file);
}