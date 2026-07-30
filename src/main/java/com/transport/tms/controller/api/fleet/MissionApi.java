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

    @PostMapping
    ResponseEntity<MissionResponse> create(@Valid @RequestBody MissionRequest request);

    @PutMapping("/{id}")
    ResponseEntity<MissionResponse> update(@PathVariable Long id,
                                           @Valid @RequestBody MissionRequest request);

/*    @PatchMapping("/{id}/soumettre")
    ResponseEntity<MissionResponse> soumettre(@PathVariable Long id);

    @PatchMapping("/{id}/approuver")
    ResponseEntity<MissionResponse> approuver(@PathVariable Long id);

    @PatchMapping("/{id}/rejeter")
    ResponseEntity<MissionResponse> rejeter(@PathVariable Long id,
                                          @RequestParam String motif);

 */

    @PatchMapping("/{id}/demarrer")
    ResponseEntity<MissionResponse> demarrer(@PathVariable Long id);

    @PatchMapping("/{id}/cloturer")
    ResponseEntity<MissionResponse> cloturer(@PathVariable Long id);

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

    @GetMapping("/vehicule/{vehiculeId}")
    ResponseEntity<List<MissionResponse>> findByVehicule(@PathVariable Long vehiculeId);

    @GetMapping("/chauffeur/{chauffeurId}")
    ResponseEntity<List<MissionResponse>> findByChauffeur(@PathVariable Long chauffeurId);

    @GetMapping("/en-cours")
    ResponseEntity<List<MissionResponse>> findEnCours();
    @GetMapping("/mes-missions")
    ResponseEntity<List<MissionResponse>> findMesMissions();
}