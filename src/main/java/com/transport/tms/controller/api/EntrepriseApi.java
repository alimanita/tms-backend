package com.transport.tms.controller.api;

import com.transport.tms.dto.EntrepriseDto;
import com.transport.tms.dto.EntrepriseRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.transport.tms.utils.Constants.ENTREPRISE_ENDPOINT;


@Tag(name = "Entreprises", description = "Gestion des entreprises")
public interface EntrepriseApi {

  @PostMapping(ENTREPRISE_ENDPOINT + "/register")
  @Operation(summary = "Enregistrer une nouvelle entreprise", description = "Créer une nouvelle entreprise avec un utilisateur administrateur")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entreprise créée avec succès"),
          @ApiResponse(responseCode = "400", description = "Données non valides"),
          @ApiResponse(responseCode = "409", description = "Entreprise existe déjà")
  })
  EntrepriseDto register(@RequestBody EntrepriseRegistrationDto dto);

  @PostMapping(ENTREPRISE_ENDPOINT + "/create")
  @Operation(summary = "Créer ou modifier une entreprise", description = "Enregistrer ou modifier une entreprise")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entreprise créée ou modifiée"),
          @ApiResponse(responseCode = "400", description = "Entreprise non valide")
  })
  EntrepriseDto save(@RequestBody EntrepriseDto dto);

  @GetMapping(ENTREPRISE_ENDPOINT + "/{idEntreprise}")
  @Operation(summary = "Rechercher une entreprise par ID", description = "Retourne l'entreprise correspondant à l'ID fourni")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entreprise trouvée"),
          @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
  })
  EntrepriseDto findById(@PathVariable("idEntreprise") Long id);

  @GetMapping(ENTREPRISE_ENDPOINT + "/all")
  @Operation(summary = "Lister toutes les entreprises", description = "Renvoie la liste de toutes les entreprises")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Liste des entreprises")
  })
  List<EntrepriseDto> findAll();

  @DeleteMapping(ENTREPRISE_ENDPOINT + "/delete/{idEntreprise}")
  @Operation(summary = "Supprimer une entreprise", description = "Supprime une entreprise par son ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Entreprise supprimée")
  })
  void delete(@PathVariable("idEntreprise") Long id);
}