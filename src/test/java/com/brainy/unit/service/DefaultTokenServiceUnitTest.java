package com.brainy.unit.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.brainy.TestUtils;
import com.brainy.model.entity.User;
import com.brainy.service.DefaultTokenService;

public class DefaultTokenServiceUnitTest {

    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;
    private DefaultTokenService tokenService;

    public DefaultTokenServiceUnitTest() {
        jwtEncoder = Mockito.mock();
        jwtDecoder = Mockito.mock();
        tokenService = new DefaultTokenService(jwtEncoder, jwtDecoder);
    }

    @Test
    public void shouldGenerateToken() {
        User user = TestUtils.generateRandomUser();

        Jwt jwt = Mockito.mock();

        Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(jwt);

        Mockito.when(jwt.getTokenValue()).thenReturn("token_value");

        String token = tokenService.generateToken(user);

        Mockito.verify(jwtEncoder).encode(Mockito.any());

        Assertions.assertEquals("token_value", token);
    }

    @Test
    public void shouldDecodeToken() {
        Jwt mockJwt = Mockito.mock();

        Mockito.when(jwtDecoder.decode("token_value")).thenReturn(mockJwt);

        Jwt jwt = tokenService.decodeToken("token_value");

        Mockito.verify(jwtDecoder).decode("token_value");

        Assertions.assertEquals(mockJwt, jwt);
    }

    @Test
    public void shouldReturnTokenIsExpired() {
        Jwt mockJwt = Mockito.mock();

        Mockito.when(mockJwt.getExpiresAt())
                .thenReturn(Instant.now().minus(1, ChronoUnit.MINUTES));

        boolean isExpired = tokenService.isTokenExpired(mockJwt);

        Assertions.assertTrue(isExpired);
    }

    @Test
    public void shouldReturnTokenIsNotExpired() {
        Jwt mockJwt = Mockito.mock();

        Mockito.when(mockJwt.getExpiresAt())
                .thenReturn(Instant.now().plus(1, ChronoUnit.MINUTES));

        boolean isExpired = tokenService.isTokenExpired(mockJwt);

        Assertions.assertFalse(isExpired);
    }
}
