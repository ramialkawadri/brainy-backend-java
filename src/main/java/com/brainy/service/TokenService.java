package com.brainy.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface TokenService {
    String generateToken(Authentication authentication);

    Jwt decodeToken(String token);

    boolean isTokenExpired(Jwt token);
}
