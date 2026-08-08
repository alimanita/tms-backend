package com.transport.tms.service.Impl;

import com.transport.tms.domain.entity.Entreprise;
import com.transport.tms.domain.entity.Roles;
import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.dto.ChangerMotDePasseUtilisateurDto;
import com.transport.tms.dto.RolesDto;
import com.transport.tms.dto.UtilisateurDto;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.exception.InvalidEntityException;
import com.transport.tms.repository.EntrepriseRepository;
import com.transport.tms.repository.RolesRepository;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.service.UtilisateurService;
import com.transport.tms.validator.UtilisateurValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {
  private static final String ROLE_CHAUFFEUR = "ROLE_CHAUFFEUR";
  private final UtilisateurRepository utilisateurRepository;
  private final EntrepriseRepository entrepriseRepository;
  private final RolesRepository rolesRepository;
  private final PasswordEncoder passwordEncoder;
  private final ChauffeurRepository chauffeurRepository;
  @Autowired
  public UtilisateurServiceImpl(
          UtilisateurRepository utilisateurRepository,
          EntrepriseRepository entrepriseRepository,
          RolesRepository rolesRepository,
          ChauffeurRepository chauffeurRepository,
          PasswordEncoder passwordEncoder) {
    this.utilisateurRepository = utilisateurRepository;
    this.entrepriseRepository = entrepriseRepository;
    this.rolesRepository = rolesRepository;
    this.chauffeurRepository = chauffeurRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Vérifie si l'utilisateur a le rôle CHAUFFEUR, et crée l'entité Chauffeur
   * correspondante si elle n'existe pas déjà.
   */
  private void createChauffeurIfNeeded(Utilisateur savedUtilisateur) {
    boolean isChauffeur = savedUtilisateur.getRoles().stream()
            .anyMatch(r -> ROLE_CHAUFFEUR.equalsIgnoreCase(r.getRoleName()));

    if (!isChauffeur) {
      return;
    }

    // Éviter de créer un doublon si un Chauffeur existe déjà pour cet utilisateur
    if (chauffeurRepository.findByUtilisateurId(savedUtilisateur.getId()).isPresent()) {
      log.info("ℹ️ Un chauffeur existe déjà pour l'utilisateur ID: {}", savedUtilisateur.getId());
      return;
    }

    log.info("🚚 Création du profil chauffeur pour l'utilisateur ID: {}", savedUtilisateur.getId());

    // Séparer fullName en prénom / nom (simple découpage sur le premier espace)
    String fullName = savedUtilisateur.getFullName() != null ? savedUtilisateur.getFullName().trim() : "";
    String prenom = fullName;
    String nom = "";
    int idx = fullName.indexOf(' ');
    if (idx > 0) {
      prenom = fullName.substring(0, idx);
      nom = fullName.substring(idx + 1).trim();
    }

    Chauffeur chauffeur = Chauffeur.builder()
            .prenom(prenom)
            .nom(nom.isEmpty() ? "-" : nom) // nom obligatoire (nullable = false)
            .email(savedUtilisateur.getEmail())
            .telephone(savedUtilisateur.getPhone())
            .utilisateur(savedUtilisateur)
            .build();

    Chauffeur savedChauffeur = chauffeurRepository.save(chauffeur);

    // Optionnel : mettre à jour driverId sur l'utilisateur pour lien rapide
    savedUtilisateur.setDriverId(savedChauffeur.getId());
    utilisateurRepository.save(savedUtilisateur);

    log.info("✅ Chauffeur créé - ID: {}, lié à utilisateur ID: {}",
            savedChauffeur.getId(), savedUtilisateur.getId());
  }
  /**
   * Normalise le nom d'un rôle en ajoutant le préfixe ROLE_ si nécessaire
   */
  private String normalizeRoleName(String roleName) {
    if (roleName == null || roleName.trim().isEmpty()) {
      throw new InvalidEntityException(
              "Le nom du rôle ne peut pas être vide",
              ErrorCodes.ROLE_NOT_VALID
      );
    }

    String normalized = roleName.trim().toUpperCase();
    return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
  }

  /**
   * Trouve ou crée un rôle
   */
  private Roles findOrCreateRole(String roleName) {
    String normalizedRoleName = normalizeRoleName(roleName);

    return rolesRepository.findByRoleName(normalizedRoleName)
            .orElseGet(() -> {
              log.info("📝 Création du nouveau rôle: {}", normalizedRoleName);
              Roles newRole = new Roles();
              newRole.setRoleName(normalizedRoleName);
              return rolesRepository.save(newRole);
            });
  }

  /**
   * Gère les rôles pour un utilisateur (ManyToMany)
   */
  private Set<Roles> manageRoles(Set<RolesDto> rolesDto) {
    Set<Roles> roles = new HashSet<>();

    if (rolesDto != null && !rolesDto.isEmpty()) {
      log.info("🔐 Attribution des rôles fournis");

      for (RolesDto roleDto : rolesDto) {
        Roles role;

        // Chercher par ID
        if (roleDto.getId() != null) {
          role = rolesRepository.findById(roleDto.getId())
                  .orElseThrow(() -> new EntityNotFoundException(
                          "Aucun rôle avec l'ID = " + roleDto.getId() + " n'a été trouvé"));
        }
        // Chercher ou créer par nom
        else if (StringUtils.hasLength(roleDto.getRoleName())) {
          role = findOrCreateRole(roleDto.getRoleName());
        } else {
          throw new InvalidEntityException(
                  "Rôle invalide: ni ID ni nom fourni",
                  ErrorCodes.ROLE_NOT_VALID
          );
        }

        roles.add(role);
        log.info("✅ Rôle ajouté: {}", role.getRoleName());
      }
    } else {
      // Rôle par défaut
      log.info("🔐 Attribution du rôle par défaut: ROLE_USER");
      roles.add(findOrCreateRole("USER"));
    }

    return roles;
  }

  @Override
  public UtilisateurDto save(UtilisateurDto dto) {
    log.info("💾 Sauvegarde utilisateur: {}", dto.getEmail());

    // Validation
    List<String> errors = UtilisateurValidator.validate(dto);
    if (!errors.isEmpty()) {
      log.error("❌ Validation échouée: {}", errors);
      throw new InvalidEntityException("L'utilisateur n'est pas valide",
              ErrorCodes.UTILISATEUR_NOT_VALID, errors);
    }

    // Vérifier l'unicité pour un nouvel utilisateur
    if (dto.getId() == null) {
      if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
        log.error("❌ Email déjà existant: {}", dto.getEmail());
        throw new InvalidEntityException("Un utilisateur avec cet email existe déjà",
                ErrorCodes.UTILISATEUR_ALREADY_EXISTS);
      }

      if (StringUtils.hasLength(dto.getUsername()) &&
              utilisateurRepository.findByUsername(dto.getUsername()).isPresent()) {
        log.error("❌ Username déjà existant: {}", dto.getUsername());
        throw new InvalidEntityException("Un utilisateur avec ce username existe déjà",
                ErrorCodes.UTILISATEUR_ALREADY_EXISTS);
      }
    }

    // Encoder le mot de passe si présent
    if (StringUtils.hasLength(dto.getPassword())) {
      dto.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    // Convertir et gérer les rôles
    Utilisateur utilisateur = UtilisateurDto.toEntity(dto);
    Set<Roles> roles = manageRoles(dto.getRoles());
    utilisateur.setRoles(roles);

    // Sauvegarder
    Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
    log.info("✅ Utilisateur sauvegardé - ID: {}, Rôles: {}",
            savedUtilisateur.getId(),
            savedUtilisateur.getRoles().stream()
                    .map(Roles::getRoleName)
                    .collect(Collectors.joining(", "))
    );
    createChauffeurIfNeeded(savedUtilisateur);
    return UtilisateurDto.fromEntity(savedUtilisateur);
  }

  @Override
  public UtilisateurDto createUtilisateur(UtilisateurDto dto, Long idEntreprise) {
    log.info("📝 Création utilisateur pour entreprise ID: {}", idEntreprise);

    // Validation
    List<String> errors = UtilisateurValidator.validate(dto);
    if (!errors.isEmpty()) {
      log.error("❌ Validation échouée: {}", errors);
      throw new InvalidEntityException("L'utilisateur n'est pas valide",
              ErrorCodes.UTILISATEUR_NOT_VALID, errors);
    }

    // Vérifier que l'entreprise existe
    Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucune entreprise avec l'ID = " + idEntreprise + " n'a été trouvée"));

    // Vérifier l'unicité de l'email
    if (utilisateurRepository.findByEmail(dto.getEmail()).isPresent()) {
      log.error("❌ Email déjà existant: {}", dto.getEmail());
      throw new InvalidEntityException("Un utilisateur avec cet email existe déjà",
              ErrorCodes.UTILISATEUR_ALREADY_EXISTS);
    }

    // Vérifier l'unicité du username si fourni
    if (StringUtils.hasLength(dto.getUsername()) &&
            utilisateurRepository.findByUsername(dto.getUsername()).isPresent()) {
      log.error("❌ Username déjà existant: {}", dto.getUsername());
      throw new InvalidEntityException("Un utilisateur avec ce username existe déjà",
              ErrorCodes.UTILISATEUR_ALREADY_EXISTS);
    }

    // Validation du mot de passe
    if (!StringUtils.hasLength(dto.getPassword())) {
      throw new InvalidEntityException("Le mot de passe est obligatoire",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    // Créer l'utilisateur
    Utilisateur utilisateur = UtilisateurDto.toEntity(dto);
    utilisateur.setPassword(passwordEncoder.encode(dto.getPassword()));
    utilisateur.setEntreprise(entreprise);

    // Gérer les rôles
    Set<Roles> roles = manageRoles(dto.getRoles());
    utilisateur.setRoles(roles);

    // Sauvegarder
    Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
    log.info("✅ Utilisateur créé - ID: {}, Username: {}, Rôles: {}",
            savedUtilisateur.getId(),
            savedUtilisateur.getUsername(),
            savedUtilisateur.getRoles().stream()
                    .map(Roles::getRoleName)
                    .collect(Collectors.joining(", "))
    );

    return UtilisateurDto.fromEntity(savedUtilisateur);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UtilisateurDto> findByRoleAndEntreprise(String role, Long idEntreprise) {
    String normalizedRole = normalizeRoleName(role);
    return utilisateurRepository.findByRoleAndEntreprise(normalizedRole, idEntreprise)
            .stream()
            .map(UtilisateurDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UtilisateurDto> findByRole(String role) {
    String normalizedRole = normalizeRoleName(role);
    return utilisateurRepository.findByRole(normalizedRole)
            .stream()
            .map(UtilisateurDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public UtilisateurDto findById(Long id) {
    log.info("🔍 Recherche utilisateur par ID: {}", id);

    if (id == null) {
      log.error("❌ ID null");
      throw new InvalidEntityException("L'ID de l'utilisateur est null",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    return utilisateurRepository.findById(id)
            .map(UtilisateurDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun utilisateur avec l'ID = " + id + " n'a été trouvé"));
  }

  @Override
  public UtilisateurDto findByEmail(String email) {
    log.info("🔍 Recherche utilisateur par email: {}", email);

    if (!StringUtils.hasLength(email)) {
      log.error("❌ Email null ou vide");
      throw new InvalidEntityException("L'email de l'utilisateur est null ou vide",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    return utilisateurRepository.findByEmail(email)
            .map(UtilisateurDto::fromEntity)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun utilisateur avec l'email = " + email + " n'a été trouvé"));
  }

  @Override
  public List<UtilisateurDto> findAllByEntreprise(Long idEntreprise) {
    log.info("🔍 Recherche utilisateurs pour entreprise ID: {}", idEntreprise);

    if (idEntreprise == null) {
      log.error("❌ ID entreprise null");
      throw new InvalidEntityException("L'ID de l'entreprise est null",
              ErrorCodes.ENTREPRISE_NOT_VALID);
    }

    return utilisateurRepository.findAllByEntrepriseId(idEntreprise).stream()
            .map(UtilisateurDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public List<UtilisateurDto> findAll() {
    log.info("🔍 Recherche tous les utilisateurs");

    return utilisateurRepository.findAll().stream()
            .map(UtilisateurDto::fromEntity)
            .collect(Collectors.toList());
  }

  @Override
  public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
    log.info("🔑 Changement mot de passe pour utilisateur ID: {}", dto.getId());

    // Validation
    if (dto.getId() == null) {
      throw new InvalidEntityException("L'ID de l'utilisateur est null",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    if (!StringUtils.hasLength(dto.getMotDePasse()) ||
            !StringUtils.hasLength(dto.getConfirmMotDePasse())) {
      throw new InvalidEntityException("Les mots de passe sont obligatoires",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    if (!dto.getMotDePasse().equals(dto.getConfirmMotDePasse())) {
      throw new InvalidEntityException("Les mots de passe ne correspondent pas",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    // Récupérer l'utilisateur
    Utilisateur utilisateur = utilisateurRepository.findById(dto.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun utilisateur avec l'ID = " + dto.getId() + " n'a été trouvé"));

    // Encoder et mettre à jour le mot de passe
    utilisateur.setPassword(passwordEncoder.encode(dto.getMotDePasse()));

    Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
    log.info("✅ Mot de passe changé pour utilisateur ID: {}", dto.getId());

    return UtilisateurDto.fromEntity(savedUtilisateur);
  }

  @Override
  public void delete(Long id) {
    log.info("🗑️ Suppression utilisateur ID: {}", id);

    if (id == null) {
      log.error("❌ ID null");
      throw new InvalidEntityException("L'ID de l'utilisateur est null",
              ErrorCodes.UTILISATEUR_NOT_VALID);
    }

    // Vérifier que l'utilisateur existe
    Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun utilisateur avec l'ID = " + id + " n'a été trouvé"));

    // ✅ Avec ManyToMany, il suffit de supprimer l'utilisateur
    // La table de jointure sera automatiquement nettoyée
    utilisateurRepository.delete(utilisateur);
    log.info("✅ Utilisateur supprimé ID: {}", id);
  }
}