package com.transport.tms.controller.fleet;

import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.rapport.*;
import com.transport.tms.service.fleet.RapportEntretiensService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * API controller exposing the same endpoints as {@link RapportEntretiensController} but under the
 * /api/v1/fleet/rapports base path expected by the front‑end.
 */
@RestController
@RequestMapping("/api/v1/fleet/rapports")
@RequiredArgsConstructor
public class RapportsApiController {

    private final RapportEntretiensService rapportService;

    // ── Entretiens / Maintenance ──────────────────────────────────────────────

    @GetMapping("/entretiens/mensuel")
    public ResponseEntity<List<MaintenanceMensuelleDto>> getEntretiensMensuel(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getRapportMensuel(entityType, d, f));
    }

    @GetMapping("/entretiens/annuel")
    public ResponseEntity<List<MaintenanceAnnuelleDto>> getEntretiensAnnuel(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(defaultValue = "0") int anDebut,
            @RequestParam(defaultValue = "0") int anFin) {
        int currentYear = LocalDate.now().getYear();
        int ad = anDebut > 0 ? anDebut : currentYear - 4;
        int af = anFin > 0 ? anFin : currentYear;
        return ResponseEntity.ok(rapportService.getRapportAnnuel(entityType, ad, af));
    }

    @GetMapping("/entretiens/detail")
    public ResponseEntity<List<MaintenanceDetailDto>> getEntretiensDetail(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getRapportDetail(entityType, d, f));
    }

    @GetMapping("/entretiens/synthese")
    public ResponseEntity<SyntheseEntretiensDto> getEntretiensSynthese(
            @RequestParam(required = false) OrdreTravail.TypeEntite entityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getSynthese(entityType, d, f));
    }

    // ── Carburant ─────────────────────────────────────────────────────────────

    @GetMapping("/carburant/mensuel")
    public ResponseEntity<List<CarburantMensuelDto>> getCarburantMensuel(
            @RequestParam(required = false) Long vehiculeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfYear(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ResponseEntity.ok(rapportService.getCarburantMensuel(vehiculeId, d, f));
    }

    @GetMapping("/carburant/annuel")
    public ResponseEntity<List<CarburantAnnuelDto>> getCarburantAnnuel(
            @RequestParam(required = false) Long vehiculeId,
            @RequestParam(defaultValue = "0") int anDebut,
            @RequestParam(defaultValue = "0") int anFin) {
        int currentYear = LocalDate.now().getYear();
        int ad = anDebut > 0 ? anDebut : currentYear - 4;
        int af = anFin > 0 ? anFin : currentYear;
        return ResponseEntity.ok(rapportService.getCarburantAnnuel(vehiculeId, ad, af));
    }
}
