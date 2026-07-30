package com.transport.tms.controller.fleet;

import com.transport.tms.domain.entity.Utilisateur;
import com.transport.tms.domain.entity.fleet.FcmToken;
import com.transport.tms.dto.fleet.request.FcmTokenRequest;
import com.transport.tms.repository.fleet.FcmTokenRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fleet/fcm-tokens")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenRepository fcmTokenRepository;

    @PostMapping
    public ResponseEntity<Void> register(@Valid @RequestBody FcmTokenRequest request, Authentication authentication) {
        Long userId = resolveUserId(authentication);

        if (fcmTokenRepository.existsByToken(request.token())) {
            return ResponseEntity.ok().build();
        }

        FcmToken fcmToken = new FcmToken();
        fcmToken.setUserId(userId);
        fcmToken.setToken(request.token());
        fcmToken.setDevice(request.device() != null ? request.device() : "WEB");
        fcmTokenRepository.save(fcmToken);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unregister(@RequestParam String token) {
        fcmTokenRepository.deleteByToken(token);
        return ResponseEntity.noContent().build();
    }

    private Long resolveUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Utilisateur utilisateur) {
            return utilisateur.getId().longValue();
        }
        throw new IllegalStateException("Principal inattendu : " + principal.getClass());
    }
}