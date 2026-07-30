package com.transport.tms.config;

import com.transport.tms.domain.entity.Roles;
import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.repository.RolesRepository;
import com.transport.tms.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UtilisateurRepository utilisateurRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.login:admin}")
    private String adminLogin;

    @Value("${app.admin.email:admin@luky}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.nom:Administrator}")
    private String adminNom;

    @Value("${app.admin.prenom:Super}")
    private String adminPrenom;

    @Bean
    public CommandLineRunner initSuperAdmin() {
        return args -> {
            log.info("Vérification de l'existence du super admin...");

            Optional<Utilisateur> existingUser = utilisateurRepository.findByUsername(adminLogin);

            if (existingUser.isPresent()) {
                log.info("Super admin '{}' existe déjà", adminLogin);
                return;
            }

            log.info("Création du super admin '{}'...", adminLogin);

            try {
                // 1. Créer ou récupérer le rôle SUPERADMIN
                Roles superAdminRole = rolesRepository.findByRoleName("SUPER_ADMIN")
                        .orElseGet(() -> {
                            log.info("Création du rôle SUPERADMIN");
                            Roles role = new Roles();
                            role.setRoleName("SUPER_ADMIN");
                            return rolesRepository.save(role);
                        });

                // 2. Créer l'utilisateur
                Utilisateur superAdmin = new Utilisateur();
                superAdmin.setUsername(adminLogin);
                superAdmin.setEmail(adminEmail);
                superAdmin.setFullName(adminPrenom + " " + adminNom);
                superAdmin.setPassword(passwordEncoder.encode(adminPassword));
                superAdmin.setActive(true);
                superAdmin.setRoles(new HashSet<>());

                // 3. Associer le rôle
                superAdmin.getRoles().add(superAdminRole);

                // 4. Sauvegarder
                utilisateurRepository.save(superAdmin);

                log.info("Super admin créé avec succès !");
                log.info("   Login: {}", adminLogin);
                log.info("   Nom: {}", superAdmin.getFullName());
                log.info("   IMPORTANT: Changez le mot de passe après la première connexion !");

            } catch (Exception e) {
                log.error("Erreur lors de la création du super admin: {}", e.getMessage(), e);
                throw e;
            }
        };
    }
}