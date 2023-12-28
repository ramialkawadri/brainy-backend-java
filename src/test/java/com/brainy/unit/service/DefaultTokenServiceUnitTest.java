package com.brainy.unit.service;

import static org.mockito.ArgumentMatchers.argThat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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
		// Arrange
		User user = TestUtils.generateRandomUser();

		Jwt jwt = Mockito.mock();

		Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(jwt);

		Mockito.when(jwt.getTokenValue()).thenReturn("token_value");

		// Act
		String token = tokenService.generateToken(user);

		// Assert
		Mockito.verify(jwtEncoder)
				.encode(argThat((ArgumentMatcher<JwtEncoderParameters>) encoderParameter -> {
					JwtClaimsSet claims = encoderParameter.getClaims();

					// Adding 1 to include the current day
					long tokenDuration =
							ChronoUnit.DAYS.between(Instant.now(), claims.getExpiresAt()) + 1;

					return claims.getSubject().equals(user.getUsername())
							&& tokenDuration == DefaultTokenService.TOKEN_DURATION;
				}));

		Assertions.assertEquals("token_value", token);
	}

	@Test
	public void shouldDecodeToken() {
		// Arrange
		Jwt mockJwt = Mockito.mock();

		Mockito.when(jwtDecoder.decode("token_value")).thenReturn(mockJwt);

		// Act
		Jwt jwt = tokenService.decodeToken("token_value");

		// Assert
		Mockito.verify(jwtDecoder).decode("token_value");
		Assertions.assertEquals(mockJwt, jwt);
	}

	@Test
	public void shouldReturnTokenIsExpired() {
		// Arrange
		Jwt mockJwt = Mockito.mock();

		Mockito.when(mockJwt.getExpiresAt()).thenReturn(Instant.now().minus(1, ChronoUnit.MINUTES));

		// Act
		boolean isExpired = tokenService.isTokenExpired(mockJwt);

		// Assert
		Assertions.assertTrue(isExpired);
	}

	@Test
	public void shouldReturnTokenIsNotExpired() {
		// Arrange
		Jwt mockJwt = Mockito.mock();

		Mockito.when(mockJwt.getExpiresAt()).thenReturn(Instant.now().plus(1, ChronoUnit.MINUTES));

		// Act
		boolean isExpired = tokenService.isTokenExpired(mockJwt);

		// Assert
		Assertions.assertFalse(isExpired);
	}
}
