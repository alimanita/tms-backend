package com.transport.tms.service.Impl;

import com.transport.tms.domain.entity.Entreprise;
import com.transport.tms.domain.entity.Roles;
import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.dto.AdresseDto;
import com.transport.tms.dto.EntrepriseDto;
import com.transport.tms.dto.EntrepriseRegistrationDto;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.exception.InvalidEntityException;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.repository.EntrepriseRepository;
import com.transport.tms.repository.RolesRepository;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.service.EntrepriseService;
import com.transport.tms.validator.EntrepriseValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class EntrepriseServiceImpl implements EntrepriseService {

  private static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final EntrepriseRepository entrepriseRepository;
  private final UtilisateurRepository utilisateurRepository;
  private final RolesRepository rolesRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public EntrepriseServiceImpl(
          EntrepriseRepository entrepriseRepository,
          UtilisateurRepository utilisateurRepository,
          RolesRepository rolesRepository,
          PasswordEncoder passwordEncoder
  ) {
    this.entrepriseRepository = entrepriseRepository;
    this.utilisateurRepository = utilisateurRepository;
    this.rolesRepository = rolesRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public EntrepriseDto register(EntrepriseRegistrationDto dto) {
    log.info("Enregistrement d'une nouvelle entreprise: {}", dto.getNom());

    if (dto == null) {
      throw new InvalidEntityException(
              "Les données de l'entreprise ne peuvent pas être nulles",
              ErrorCodes.ENTREPRISE_NOT_VALID
      );
    }

    if (dto.getUtilisateurAdmin() == null) {
      throw new InvalidEntityException(
              "Les informations de l'administrateur sont obligatoires",
              ErrorCodes.ENTREPRISE_NOT_VALID
      );
    }

    if (entrepriseRepository.existsByEmail(dto.getEmail())) {
      throw new InvalidEntityException(
              "Une entreprise avec cet email existe déjà",
              ErrorCodes.ENTREPRISE_ALREADY_EXISTS
      );
    }

    if (entrepriseRepository.existsByMatriculeFiscal(dto.getMatriculeFiscal())) {
      throw new InvalidEntityException(
              "Une entreprise avec ce matricule fiscal existe déjà",
              ErrorCodes.ENTREPRISE_ALREADY_EXISTS
      );
    }

    if (utilisateurRepository.findByEmail(dto.getUtilisateurAdmin().getEmail()).isPresent()) {
      throw new InvalidEntityException(
              "Un utilisateur avec cet email existe déjà",
              ErrorCodes.UTILISATEUR_ALREADY_EXISTS
      );
    }

    if (utilisateurRepository.findByUsername(dto.getUtilisateurAdmin().getLogin()).isPresent()) {
      throw new InvalidEntityException(
              "Un utilisateur avec ce login existe déjà",
              ErrorCodes.UTILISATEUR_ALREADY_EXISTS
      );
    }

    // Créer l'entreprise
    Entreprise entreprise = new Entreprise();
    entreprise.setNom(dto.getNom());
    entreprise.setDescription(dto.getDescription());
    entreprise.setEmail(dto.getEmail());
    entreprise.setNumTel(dto.getNumTel());
    entreprise.setMatriculeFiscal(dto.getMatriculeFiscal());
    entreprise.setCodeFiscal(dto.getCodeFiscal());
    entreprise.setSteWeb(dto.getSteWeb());
    entreprise.setAdresse(AdresseDto.toEntity(dto.getAdresse()));
    entreprise.setActive(true);

    Entreprise savedEntreprise = entrepriseRepository.save(entreprise);
    log.info("Entreprise créée avec l'ID: {}", savedEntreprise.getId());

    // Récupérer (ou créer) le rôle ADMIN
    Roles adminRole = rolesRepository.findByRoleName(ROLE_ADMIN)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Le rôle " + ROLE_ADMIN + " n'existe pas"));

    // Créer l'utilisateur administrateur — champs alignés sur l'entité Utilisateur
    String fullName = (dto.getUtilisateurAdmin().getPrenom() + " " + dto.getUtilisateurAdmin().getNom()).trim();

    Set<Roles> roles = new HashSet<>();
    roles.add(adminRole);

    Utilisateur admin = Utilisateur.builder()
            .username(dto.getUtilisateurAdmin().getLogin())
            .fullName(fullName)
            .email(dto.getUtilisateurAdmin().getEmail())
            .password(passwordEncoder.encode(dto.getUtilisateurAdmin().getPassword()))
            .entreprise(savedEntreprise)
            .roles(roles)
            .active(true)
            .build();

    Utilisateur savedAdmin = utilisateurRepository.save(admin);
    log.info("Utilisateur administrateur créé avec l'ID: {} pour l'entreprise: {}",
            savedAdmin.getId(), savedEntreprise.getNom());

    return EntrepriseDto.fromEntity(savedEntreprise);
  }

  @Override
  public EntrepriseDto save(EntrepriseDto dto) {
    List<String> errors = EntrepriseValidator.validate(dto);
    if (!errors.isEmpty()) {
      log.error("Entreprise n'est pas valide {}", dto);
      throw new InvalidEntityException(
              "L'entreprise n'est pas valide",
              ErrorCodes.ENTREPRISE_NOT_VALID,
              errors
      );
    }

    if (dto.getId() == null && entrepriseRepository.existsByEmail(dto.getEmail())) {
      throw new InvalidEntityException(
              "Une entreprise avec cet email existe déjà",
              ErrorCodes.ENTREPRISE_ALREADY_EXISTS
      );
    }

    if (dto.getId() == null && entrepriseRepository.existsByMatriculeFiscal(dto.getMatriculeFiscal())) {
      throw new InvalidEntityException(
              "Une entreprise avec ce matricule fiscal existe déjà",
              ErrorCodes.ENTREPRISE_ALREADY_EXISTS
      );
    }

    return EntrepriseDto.fromEntity(
            entrepriseRepository.save(EntrepriseDto.toEntity(dto))
    );
  }

  @Override
  public EntrepriseDto findById(Long id) {
    if (id == null) {
      log.error("Entreprise ID est null");
      throw new InvalidEntityException("L'ID de l'entreprise est null", ErrorCodes.ENTREPRISE_NOT_VALID);
    }

    return entrepriseRepository.findById(id)
            .map(EntrepriseDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune entreprise avec l'ID = " + id + " n'a été trouvée"));
  }

  @Override
  public EntrepriseDto findByEmail(String email) {
    if (email == null || email.isEmpty()) {
      log.error("Entreprise email est null");
      throw new InvalidEntityException("L'email de l'entreprise est null", ErrorCodes.ENTREPRISE_NOT_VALID);
    }

    return entrepriseRepository.findByEmail(email)
            .map(EntrepriseDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune entreprise avec l'email = " + email + " n'a été trouvée"));
  }

  @Override
  public EntrepriseDto findByMatriculeFiscal(String matriculeFiscal) {
    if (matriculeFiscal == null || matriculeFiscal.isEmpty()) {
      log.error("Matricule fiscal est null");
      throw new InvalidEntityException("Le matricule fiscal est null", ErrorCodes.ENTREPRISE_NOT_VALID);
    }

    return entrepriseRepository.findByMatriculeFiscal(matriculeFiscal)
            .map(EntrepriseDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune entreprise avec le matricule fiscal = " + matriculeFiscal + " n'a été trouvée"));
  }

  @Override
  public List<EntrepriseDto> findAll() {
    return entrepriseRepository.findAll().stream()
            .map(EntrepriseDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public void delete(Long id) {
    if (id == null) {
      log.error("Entreprise ID est null");
      throw new InvalidEntityException("L'ID de l'entreprise est null", ErrorCodes.ENTREPRISE_NOT_VALID);
    }

    if (!utilisateurRepository.findAllByEntrepriseId(id).isEmpty()) {
      throw new InvalidOperationException(
              "Impossible de supprimer cette entreprise qui possède des utilisateurs",
              ErrorCodes.ENTREPRISE_ALREADY_IN_USE
      );
    }

    entrepriseRepository.deleteById(id);
  }
}