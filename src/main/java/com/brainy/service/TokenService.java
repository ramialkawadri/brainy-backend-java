package com.brainy.service;

import org.springframework.security.oauth2.jwt.Jwt;

import com.brainy.model.entity.User;

public interface TokenService {
	String generateToken(User user);

	Jwt decodeToken(String token);

	boolean isTokenExpired(Jwt token);
}
