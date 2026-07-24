package com.transport.tms.controller;

import com.transport.tms.dto.request.ChauffeurRequest;
import com.transport.tms.dto.response.ChauffeurResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.service.ChauffeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fleet/chauffeurs")
@RequiredArgsConstructor
public class ChauffeurController {

    private final ChauffeurService chauffeurService;

    @GetMapping
    public PageResponse<ChauffeurResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return chauffeurService.list(page, size);
    }

    @GetMapping("/{id}")
    public ChauffeurResponse getById(@PathVariable Long id) {
        return chauffeurService.getById(id);
    }

    @GetMapping("/disponibles")
    public List<ChauffeurResponse> getDisponibles() {
        return chauffeurService.getDisponibles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChauffeurResponse create(@Valid @RequestBody ChauffeurRequest request) {
        return chauffeurService.create(request);
    }

    @PutMapping("/{id}")
    public ChauffeurResponse update(@PathVariable Long id, @Valid @RequestBody ChauffeurRequest request) {
        return chauffeurService.update(id, request);
    }

    @PatchMapping("/{id}/toggle-actif")
    public ChauffeurResponse toggleActif(@PathVariable Long id) {
        return chauffeurService.toggleActif(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        chauffeurService.delete(id);
    }
}
