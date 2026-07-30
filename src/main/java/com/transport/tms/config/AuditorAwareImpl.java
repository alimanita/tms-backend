package com.transport.tms.config;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof com.transport.tms.security.UserPrincipal userPrincipal) {
            return Optional.ofNullable(userPrincipal.getId());
        }

        if (principal instanceof Utilisateur utilisateur) {
            return Optional.ofNullable(utilisateur.getId());
        }

        return Optional.empty();
    }
}