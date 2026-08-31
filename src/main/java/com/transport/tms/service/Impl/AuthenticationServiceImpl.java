package com.transport.tms.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.dto.*;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.exception.InvalidEntityException;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.service.AuthenticationService;
import com.transport.tms.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ChauffeurRepository chauffeurRepository;
    @Autowired
    private final UserDetailsService userDetailsService;
    @Value("classpath:menu.json")
    private Resource menuResource;
    @Autowired
    public AuthenticationServiceImpl(
            UtilisateurRepository utilisateurRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            ChauffeurRepository chauffeurRepository
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.chauffeurRepository = chauffeurRepository;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = request.getEmail().trim();
        log.info("Authentification avec email: {}", email);

        try {
            // 1) Authentifier avec Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );

            log.info("Authentification Spring Security réussie pour: {}", email);

            // 2) Récupérer l'utilisateur depuis la base
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("Utilisateur non trouvé dans la base: {}", email);
                        return new EntityNotFoundException("Utilisateur non trouvé");
                    });

            log.info("Utilisateur trouvé: ID={}, Email={}", utilisateur.getId(), utilisateur.getEmail());

            // 3) Générer les tokens JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            log.info("Tokens JWT générés avec succès pour: {}", email);

            // 4) Construire la réponse
            UtilisateurDto utilisateurDto = UtilisateurDto.fromEntity(utilisateur);
            utilisateurDto.setPassword(null);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .utilisateur(utilisateurDto)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Échec d'authentification pour: {} - Mauvais credentials", email);
            throw new InvalidEntityException(
                    "Email ou mot de passe incorrect",
                    ErrorCodes.UTILISATEUR_NOT_VALID
            );
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'authentification: {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Erreur lors de l'authentification: " + e.getMessage(),
                    ErrorCodes.UTILISATEUR_NOT_VALID
            );
        }
    }

    @Override
    public UtilisateurDto getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Utilisateur utilisateur = utilisateurRepository.findByEmail(userDetails.getUsername())
                .or(() -> utilisateurRepository.findByUsername(userDetails.getUsername()))
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        UtilisateurDto dto = UtilisateurDto.fromEntity(utilisateur);
        dto.setPassword(null);

        // Enrichir avec les paramètres de visibilité si c'est un chauffeur
        enrichWithChauffeurSettings(dto, utilisateur);

        return dto;
    }

    private void enrichWithChauffeurSettings(UtilisateurDto dto, Utilisateur utilisateur) {
        try {
            chauffeurRepository.findByUtilisateurId(utilisateur.getId()).ifPresent(chauffeur -> {
                dto.setShowTarif(chauffeur.getShowTarif() != null ? chauffeur.getShowTarif() : true);
                dto.setShowCout(chauffeur.getShowCout() != null ? chauffeur.getShowCout() : true);
                dto.setShowCarburant(chauffeur.getShowCarburant() != null ? chauffeur.getShowCarburant() : true);
            });
        } catch (Exception e) {
            log.warn("Impossible de récupérer les paramètres chauffeur pour l'utilisateur {}: {}", utilisateur.getId(), e.getMessage());
        }
    }
    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        try {
            log.info("Tentative de refresh token");

            // 1. Extraire l'email du refresh token
            String email = jwtUtil.extractUsername(refreshToken);
            log.info("Email extrait du refresh token: {}", email);

            // 2. Charger l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 3. Valider le refresh token
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                log.info("Refresh token valide, génération de nouveaux tokens");

                // 4. Générer un nouveau token
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                // 5. Récupérer l'utilisateur
                Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException(
                        ));

                UtilisateurDto utilisateurDto = UtilisateurDto.fromEntity(utilisateur);
                utilisateurDto.setPassword(null);

                log.info("Nouveaux tokens générés avec succès");

                return AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .utilisateur(utilisateurDto)
                        .build();
            } else {
                log.error("Refresh token invalide");
                throw new InvalidEntityException(
                        "Refresh token invalide",
                        ErrorCodes.UNAUTHORIZED_ACCESS
                );
            }
        } catch (Exception e) {
            log.error("Erreur lors du refresh token: {}", e.getMessage(), e);
            throw new InvalidEntityException(
                    "Erreur lors du refresh du token",
                    ErrorCodes.UNAUTHORIZED_ACCESS
            );
        }
    }

    @Override
    public AuthenticationResponse registerUser(RegisterDto registerDto) {
        // TODO: Implémenter la logique d'inscription
        throw new UnsupportedOperationException("Méthode non implémentée");
    }

    @Override
    public AuthenticationResponse login(LoginDto loginDto) {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(loginDto.getLogin())
                .password(loginDto.getPassword())
                .build();
        return authenticate(request);
    }
    @Override
    public Map<String, Object> getUserMenu() {
        try {
            // Récupérer l'utilisateur actuel
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Utilisateur non authentifié - retour menu vide");
                return Map.of("menu", List.of());
            }

            // Charger l'utilisateur complet
            Utilisateur utilisateur = loadUtilisateurFromAuthentication(authentication);

            if (utilisateur == null) {
                log.error("Impossible de charger l'utilisateur");
                return getDefaultMenu();
            }

            // Extraire les rôles
            Set<String> userRoles = extractUserRoles(utilisateur);
            log.info("Utilisateur: {} - Rôles: {}", utilisateur.getEmail(), userRoles);

            // Lire le menu complet depuis le fichier JSON
            Map<String, Object> fullMenu = loadMenuFromFile();

            // Filtrer le menu selon les permissions
            List<Map<String, Object>> menuItems = (List<Map<String, Object>>) fullMenu.get("menu");
            List<Map<String, Object>> filteredMenu = filterMenuByRoles(menuItems, userRoles);

            log.info("Menu filtré: {} items pour l'utilisateur {}",
                    filteredMenu.size(),
                    utilisateur.getEmail());

            return Map.of("menu", filteredMenu);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération du menu utilisateur", e);
            return getDefaultMenu();
        }
    }

    /**
     * Charge l'utilisateur depuis l'authentification
     */
    private Utilisateur loadUtilisateurFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        log.debug("Principal class: {}", principal.getClass().getName());

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return null;
        }

        return utilisateurRepository.findByEmail(username)
                .orElseGet(() -> utilisateurRepository.findByUsername(username).orElse(null));
    }

    /**
     * Extrait les noms de rôles de l'utilisateur
     */
    private Set<String> extractUserRoles(Utilisateur utilisateur) {
        if (utilisateur.getRoles() == null) {
            return Collections.emptySet();
        }

        return utilisateur.getRoles().stream()
                .map(role -> {
                    String roleName = role.getRoleName();
                    // Enlever le préfixe ROLE_ si présent
                    if (roleName != null && roleName.startsWith("ROLE_")) {
                        return roleName.substring(5); // Enlève "ROLE_"
                    }
                    return roleName;
                })
                .collect(Collectors.toSet());
    }
    /**
     * Charge le menu depuis le fichier JSON
     */
    private Map<String, Object> loadMenuFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                menuResource.getInputStream(),
                new TypeReference<Map<String, Object>>() {}
        );
    }

    /**
     * Filtre récursivement les items du menu selon les rôles de l'utilisateur
     */
    private List<Map<String, Object>> filterMenuByRoles(
            List<Map<String, Object>> menuItems,
            Set<String> userRoles) {

        if (menuItems == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> filtered = new ArrayList<>();

        for (Map<String, Object> item : menuItems) {
            log.debug("Vérification item: {} - Permissions: {}",
                    item.get("name"),
                    item.get("permissions"));

            // Vérifier les permissions de l'item
            if (hasPermission(item, userRoles)) {
                Map<String, Object> filteredItem = new HashMap<>(item);

                // Filtrer récursivement les enfants si présents
                if (item.containsKey("children") && item.get("children") != null) {
                    List<Map<String, Object>> children =
                            (List<Map<String, Object>>) item.get("children");
                    List<Map<String, Object>> filteredChildren =
                            filterMenuByRoles(children, userRoles);

                    // Si c'est un menu de type "sub", on ne le garde que s'il a des enfants
                    if ("sub".equals(item.get("type"))) {
                        if (!filteredChildren.isEmpty()) {
                            filteredItem.put("children", filteredChildren);
                            filtered.add(filteredItem);
                            log.debug("Menu 'sub' gardé avec {} enfants: {}",
                                    filteredChildren.size(),
                                    item.get("name"));
                        } else {
                            log.debug("Menu 'sub' rejeté (aucun enfant visible): {}",
                                    item.get("name"));
                        }
                    } else {
                        filteredItem.put("children", filteredChildren);
                        filtered.add(filteredItem);
                    }
                } else {
                    // Pas d'enfants, ajouter directement
                    filtered.add(filteredItem);
                    log.debug("Item sans enfants ajouté: {}", item.get("name"));
                }
            } else {
                log.debug("Item rejeté (permissions): {}", item.get("name"));
            }
        }

        return filtered;
    }

    /**
     * Vérifie si l'utilisateur a la permission d'accéder à cet item de menu
     */
    private boolean hasPermission(Map<String, Object> menuItem, Set<String> userRoles) {
        if (!menuItem.containsKey("permissions") || menuItem.get("permissions") == null) {
            log.debug("Pas de permissions définies pour: {} - Accès autorisé",
                    menuItem.get("name"));
            return true; // Pas de restriction = accessible à tous
        }

        Map<String, Object> permissions = (Map<String, Object>) menuItem.get("permissions");

        log.debug("Vérification permissions pour: {} - User roles: {} - Required: {}",
                menuItem.get("name"),
                userRoles,
                permissions);

        // Vérifier "only" - l'utilisateur doit avoir AU MOINS un de ces rôles
        if (permissions.containsKey("only")) {
            Object only = permissions.get("only");
            List<String> requiredRoles = only instanceof List
                    ? (List<String>) only
                    : Collections.singletonList((String) only);

            log.debug("Roles requis (only): {}", requiredRoles);

            boolean hasRequiredRole = requiredRoles.stream()
                    .anyMatch(role -> {
                        boolean match = userRoles.contains(role);
                        log.debug("  Vérification role '{}': {}", role, match);
                        return match;
                    });

            if (!hasRequiredRole) {
                log.debug("Accès refusé - Aucun rôle requis trouvé");
                return false;
            }
        }

        // Vérifier "except" - l'utilisateur ne doit PAS avoir ces rôles
        if (permissions.containsKey("except")) {
            Object except = permissions.get("except");
            List<String> forbiddenRoles = except instanceof List
                    ? (List<String>) except
                    : Collections.singletonList((String) except);

            log.debug("Roles interdits (except): {}", forbiddenRoles);

            boolean hasForbiddenRole = forbiddenRoles.stream()
                    .anyMatch(role -> {
                        boolean match = userRoles.contains(role);
                        log.debug("  Vérification role interdit '{}': {}", role, match);
                        return match;
                    });

            if (hasForbiddenRole) {
                log.debug("Accès refusé - Role interdit trouvé");
                return false;
            }
        }

        log.debug("Accès autorisé");
        return true;
    }

    /**
     * Retourne un menu par défaut en cas d'erreur
     */
    private Map<String, Object> getDefaultMenu() {
        return Map.of("menu", List.of(
                Map.of(
                        "route", "dashboard",
                        "name", "dashboard",
                        "type", "link",
                        "icon", "dashboard"
                )
        ));
    }
}