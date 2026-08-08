package com.transport.tms.controller.fleet;

import com.transport.tms.domain.entity.fleet.FichePaie;
import com.transport.tms.service.fleet.FichePaieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet/fiches-paie")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FichePaieController {

    private final FichePaieService fichePaieService;

    @PostMapping("/calculer")
    public ResponseEntity<FichePaie> calculerEtGenerer(
            @RequestParam Long chauffeurId,
            @RequestParam String moisAnnee) {
        return ResponseEntity.ok(fichePaieService.calculerEtGenerer(chauffeurId, moisAnnee));
    }

    @PostMapping("/upload-manuel")
    public ResponseEntity<FichePaie> uploadManual(
            @RequestParam Long chauffeurId,
            @RequestParam String moisAnnee,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fichePaieService.uploadManual(chauffeurId, moisAnnee, file));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<FichePaie> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fichePaieService.uploadDocument(id, file));
    }

    @GetMapping("/chauffeur/{chauffeurId}")
    public ResponseEntity<List<FichePaie>> getFichesPaieByChauffeur(@PathVariable Long chauffeurId) {
        return ResponseEntity.ok(fichePaieService.getFichesPaieByChauffeur(chauffeurId));
    }
}
