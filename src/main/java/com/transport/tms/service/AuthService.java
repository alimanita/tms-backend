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

    public AuthResponse.UserResponse me(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId()).orElseThrow();
        return new AuthResponse.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRoles().stream().map(r -> r.getCode()).toList()
        );
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
