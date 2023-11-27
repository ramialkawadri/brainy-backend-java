package com.brainy.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.brainy.model.entity.User;

@Service
public class DefaultTokenService implements TokenService {

	private final JwtEncoder jwtEncoder;
	private final JwtDecoder jwtDecoder;

	public DefaultTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
	}

	@Override
	public String generateToken(User user) {
		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder().issuedAt(now)
				.expiresAt(now.plus(14, ChronoUnit.DAYS)).subject(user.getUsername()).build();

		JwtEncoderParameters encoderParameters =
				JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);

		return jwtEncoder.encode(encoderParameters).getTokenValue();
	}

	@Override
	public Jwt decodeToken(String token) {
		return jwtDecoder.decode(token);
	}

	@Override
	public boolean isTokenExpired(Jwt token) {
		Instant expireDate = token.getExpiresAt();
		return expireDate != null && expireDate.isBefore(Instant.now());
	}
}
