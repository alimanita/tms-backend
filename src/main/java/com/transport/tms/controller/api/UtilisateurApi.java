package com.transport.tms.controller.api;

import static com.transport.tms.utils.Constants.APP_ROOT;
import static com.transport.tms.utils.Constants.UTILISATEUR_ENDPOINT;

import com.transport.tms.dto.ChangerMotDePasseUtilisateurDto;
import com.transport.tms.dto.UtilisateurDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@CrossOrigin(origins = "*")
@Tag(name = "utilisateurs", description = "Gestion des utilisateurs")
public interface UtilisateurApi {



  @PostMapping(UTILISATEUR_ENDPOINT + "/create")
  UtilisateurDto save(@RequestBody UtilisateurDto dto);

  @PostMapping(UTILISATEUR_ENDPOINT + "/update/password")
  UtilisateurDto changerMotDePasse(@RequestBody ChangerMotDePasseUtilisateurDto dto);
  @GetMapping(UTILISATEUR_ENDPOINT + "/by-role")
  @Operation(
          summary = "Lister les utilisateurs par rôle",
          description = "Retourne les utilisateurs de l'entreprise ayant le rôle spécifié"
  )
  List<UtilisateurDto> findByRole(
          @RequestParam("role") String role,
          @RequestParam("idEntreprise") Long idEntreprise
  );
  @GetMapping(UTILISATEUR_ENDPOINT + "/{idUtilisateur}")
  UtilisateurDto findById(@PathVariable("idUtilisateur") Long id);

  @GetMapping(UTILISATEUR_ENDPOINT + "/find/{email}")
  UtilisateurDto findByEmail(@PathVariable("email") String email);

  @GetMapping(UTILISATEUR_ENDPOINT + "/all")
  List<UtilisateurDto> findAll();

  @DeleteMapping(UTILISATEUR_ENDPOINT + "/delete/{idUtilisateur}")
  void delete(@PathVariable("idUtilisateur") Long id);

}
