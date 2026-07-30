package com.transport.tms.service;

import com.transport.tms.dto.*;

import java.util.Map;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    UtilisateurDto getCurrentUser();

    AuthenticationResponse registerUser(RegisterDto registerDto);

    AuthenticationResponse login(LoginDto loginDto);

    AuthenticationResponse refreshToken(String refreshToken);
    Map<String, Object> getUserMenu();
}
