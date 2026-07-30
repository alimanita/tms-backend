package com.transport.tms.controller.api;

import com.transport.tms.dto.RolesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.transport.tms.utils.Constants.APP_ROOT;


@Tag(name = "Roles", description = "Gestion des rôles")
public interface RolesApi {

    @PostMapping(
            value =  APP_ROOT + "/roles/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Créer un rôle", description = "Enregistre un nouveau rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle créé"),
            @ApiResponse(responseCode = "400", description = "Rôle non valide")
    })
    RolesDto save(@RequestBody RolesDto dto);

    @GetMapping(
            value = APP_ROOT + "/roles/{idRole}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Trouver un rôle par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle trouvé"),
            @ApiResponse(responseCode = "404", description = "Rôle non trouvé")
    })
    RolesDto findById(@PathVariable("idRole") Long idRole);

    @GetMapping(
            value = APP_ROOT + "/roles/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Lister tous les rôles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rôles")
    })
    List<RolesDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/roles/delete/{idRole}")
    @Operation(summary = "Supprimer un rôle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rôle supprimé")
    })
    void delete(@PathVariable("idRole") Long idRole);
}