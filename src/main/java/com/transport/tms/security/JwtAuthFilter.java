package com.transport.tms.security;
import com.transport.tms.utils.JwtUtil;
import com.transport.tms.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // ✅ AJOUT : court-circuite complètement le filtre pour les routes publiques
    @Override
// changement
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();


        return path.startsWith("/gestiondestock/v1/doc/")

                || path.startsWith("/doc/")                          // ✅ si context-path configuré
                || path.equals("/gestiondestock/v1/auth/login")
                || path.startsWith("/auth/")                         // ✅ si context-path configuré
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.info("🔍 JWT Filter - Auth Header: {}", authHeader);

        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.info("✅ Token extrait (premiers 20 chars): {}...",
                    jwt.substring(0, Math.min(20, jwt.length())));

            try {
                username = jwtUtil.extractUsername(jwt);
                log.info("✅ Username extrait du token: {}", username);
            } catch (Exception e) {
                log.error("❌ Erreur extraction username: {}", e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ Pas de header Authorization ou ne commence pas par 'Bearer '");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("🔍 Chargement UserDetails pour: {}", username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("✅ UserDetails chargé: {}", userDetails.getUsername());
                log.info("✅ Authorities: {}", userDetails.getAuthorities());

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    log.info("✅ Token validé avec succès");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("✅ Authentication mise dans SecurityContext");
                    log.info("✅ Authenticated user: {}",
                            SecurityContextHolder.getContext().getAuthentication().getName());
                    log.info("✅ Authorities: {}",
                            SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    log.error("❌ Token invalide");
                }
            } catch (Exception e) {
                log.error("❌ Erreur lors de la validation: {}", e.getMessage(), e);
            }
        } else {
            if (username == null) {
                log.warn("⚠️ Username est null - pas d'authentification");
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("ℹ️ Authentication déjà présente dans SecurityContext");
            }
        }

        log.info("========================================");
        chain.doFilter(request, response);
    }
}