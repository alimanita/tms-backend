package com.transport.tms.service;

import com.transport.tms.domain.entity.RefreshToken;
import com.transport.tms.domain.entity.User;
import com.transport.tms.dto.request.LoginRequest;
import com.transport.tms.dto.response.AuthResponse;
import com.transport.tms.repository.RefreshTokenRepository;
import com.transport.tms.repository.UserRepository;
import com.transport.tms.security.JwtService;
import com.transport.tms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${tms.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${tms.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build());

        return new AuthResponse(
                accessToken,
                refreshToken,
                accessTokenExpirationMs,
                new AuthResponse.UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRoles().stream().map(r -> r.getCode()).toList()
                )
        );
    }

    @Value("classpath:menu.json")
    private org.springframework.core.io.Resource menuResource;

    public AuthResponse.UserResponse me(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        return new AuthResponse.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRoles().stream().map(r -> r.getCode()).toList()
        );
    }

    public java.util.Map<String, Object> getUserMenu(UserPrincipal principal) {
        try {
            User user = userRepository.findById(principal.getId()).orElse(null);
            java.util.Set<String> userRoles = new java.util.HashSet<>();
            if (user != null && user.getRoles() != null) {
                user.getRoles().forEach(r -> {
                    String code = r.getCode();
                    if (code != null) {
                        if (code.startsWith("ROLE_")) {
                            code = code.substring(5);
                        }
                        userRoles.add(code);
                    }
                });
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> fullMenu = mapper.readValue(
                    menuResource.getInputStream(),
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}
            );

            java.util.List<java.util.Map<String, Object>> menuItems = (java.util.List<java.util.Map<String, Object>>) fullMenu.get("menu");
            java.util.List<java.util.Map<String, Object>> filteredMenu = filterMenuByRoles(menuItems, userRoles);

            return java.util.Map.of("menu", filteredMenu);
        } catch (Exception e) {
            return java.util.Map.of("menu", java.util.List.of());
        }
    }

    private java.util.List<java.util.Map<String, Object>> filterMenuByRoles(
            java.util.List<java.util.Map<String, Object>> menuItems,
            java.util.Set<String> userRoles) {

        if (menuItems == null) {
            return java.util.Collections.emptyList();
        }

        java.util.List<java.util.Map<String, Object>> filtered = new java.util.ArrayList<>();

        for (java.util.Map<String, Object> item : menuItems) {
            if (hasPermission(item, userRoles)) {
                java.util.Map<String, Object> filteredItem = new java.util.HashMap<>(item);
                if (item.containsKey("children") && item.get("children") != null) {
                    java.util.List<java.util.Map<String, Object>> children =
                            (java.util.List<java.util.Map<String, Object>>) item.get("children");
                    java.util.List<java.util.Map<String, Object>> filteredChildren =
                            filterMenuByRoles(children, userRoles);

                    if ("sub".equals(item.get("type"))) {
                        if (!filteredChildren.isEmpty()) {
                            filteredItem.put("children", filteredChildren);
                            filtered.add(filteredItem);
                        }
                    } else {
                        filteredItem.put("children", filteredChildren);
                        filtered.add(filteredItem);
                    }
                } else {
                    filtered.add(filteredItem);
                }
            }
        }
        return filtered;
    }

    private boolean hasPermission(java.util.Map<String, Object> menuItem, java.util.Set<String> userRoles) {
        if (!menuItem.containsKey("permissions") || menuItem.get("permissions") == null) {
            return true;
        }

        java.util.Map<String, Object> permissions = (java.util.Map<String, Object>) menuItem.get("permissions");

        if (permissions.containsKey("only")) {
            Object only = permissions.get("only");
            java.util.List<String> requiredRoles = only instanceof java.util.List
                    ? (java.util.List<String>) only
                    : java.util.Collections.singletonList((String) only);

            boolean hasRequiredRole = requiredRoles.stream().anyMatch(userRoles::contains);
            if (!hasRequiredRole) return false;
        }

        if (permissions.containsKey("except")) {
            Object except = permissions.get("except");
            java.util.List<String> forbiddenRoles = except instanceof java.util.List
                    ? (java.util.List<String>) except
                    : java.util.Collections.singletonList((String) except);

            boolean hasForbiddenRole = forbiddenRoles.stream().anyMatch(userRoles::contains);
            if (hasForbiddenRole) return false;
        }

        return true;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

