package com.transport.tms.controller;

import com.transport.tms.dto.request.LoginRequest;
import com.transport.tms.dto.response.AuthResponse;
import com.transport.tms.security.UserPrincipal;
import com.transport.tms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthResponse.UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.me(principal);
    }

    @GetMapping("/menu")
    public java.util.Map<String, Object> getUserMenu(@AuthenticationPrincipal UserPrincipal principal) {
        return authService.getUserMenu(principal);
    }
}

