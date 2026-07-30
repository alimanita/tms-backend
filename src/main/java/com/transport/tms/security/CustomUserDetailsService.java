package com.transport.tms.security;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Chercher d'abord par email, puis par username
        Utilisateur utilisateur = utilisateurRepository.findByEmail(identifier)
                .or(() -> utilisateurRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec l'identifiant : " + identifier));

        return new UserPrincipal(
                utilisateur.getId(),
                utilisateur.getUsername(),
                utilisateur.getPassword(),
                utilisateur.getFullName(),
                utilisateur.isActive(),
                utilisateur.getDriverId(),
                utilisateur.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(
                                role.getRoleName().startsWith("ROLE_")
                                        ? role.getRoleName()
                                        : "ROLE_" + role.getRoleName()
                        ))
                        .collect(Collectors.toList())
        );
    }
}

