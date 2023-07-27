package com.brainy.service.Implementation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.brainy.service.TokenService;

@Service
public class DefaultTokenService implements TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public DefaultTokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(10, ChronoUnit.DAYS))
                .subject(authentication.getName())
                .build();

        JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(), claims
        );

        return jwtEncoder.encode(encoderParameters)
                .getTokenValue();
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
