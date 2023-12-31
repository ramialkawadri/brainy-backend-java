package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.TestUtils;
import com.brainy.integration.IntegrationTest;
import com.brainy.integration.IntegrationTestUtils;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdatePasswordRequest;

public class AuthControllerIntegrationTest extends IntegrationTest {

	@Test
	public void shouldRegisterUser() {
		// Arrange
		User tmpUser = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, tmpUser);

		// Act
		ResponseEntity<ResponseString> response =
				restTemplate.withBasicAuth(testUser.getUsername(), testUser.getPassword())
						.postForEntity("/token", null, ResponseString.class);

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void shouldAuthorizeRequestsWithUpperCaseUsername() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		IntegrationTestUtils.registerUser(restTemplate, user);
		user.setUsername(user.getUsername().toUpperCase());

		// Act
		ResponseEntity<ResponseString> response =
				IntegrationTestUtils.getAuthenticatedRestTemplateForUser(restTemplate, user)
						.postForEntity("/token", null, ResponseString.class);

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void shouldNotAuthorize() {
		// Arrange & Act
		ResponseEntity<Void> response = restTemplate.postForEntity("/token", null, Void.class);

		// Assert
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void shouldNotAuthorizeTokenAfterLogout() {
		// Arrange
		String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.set("Cookie", "test=test");

		// Act
		ResponseEntity<Void> logoutResponse = restTemplate.exchange("/logout", HttpMethod.POST,
				new HttpEntity<>(headers), Void.class);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/token", HttpMethod.POST,
				new HttpEntity<>(headers), Void.class);

		// Assert
		Assertions.assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
	}

	@Test
	public void shouldNotAuthorizeTokenAfterPasswordChange() {
		// Arrange
		String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		UpdatePasswordRequest body = new UpdatePasswordRequest("StrongPass1");

		// Act
		ResponseEntity<Void> logoutResponse = restTemplate.exchange("/password", HttpMethod.POST,
				new HttpEntity<>(body, headers), Void.class);

		ResponseEntity<Void> loginResponse = restTemplate.exchange("/token", HttpMethod.POST,
				new HttpEntity<>(headers), Void.class);

		// Assert
		Assertions.assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
	}

	@Test
	public void shouldAuthorizeUsingHeaders() {
		// Arrange & Act
		ResponseEntity<ResponseString> response =
				restTemplate.withBasicAuth(testUser.getUsername(), testUser.getPassword())
						.postForEntity("/token", null, ResponseString.class);

		ResponseString responseBody = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
	}

	@Test
	public void shouldNotAuthorizeBecauseOfBadToken() {
		// Arrange
		String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", "token=" + token + "RANDOM_STRING");

		// Act
		ResponseEntity<Void> response = restTemplate.exchange("/token", HttpMethod.POST,
				new HttpEntity<>(headers), Void.class);

		// Assert
		Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void shouldAuthorizeUsingJwtCookies() {
		// Arrange
		String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", "token=" + token);

		// Act
		ResponseEntity<ResponseString> response = restTemplate.exchange("/token", HttpMethod.POST,
				new HttpEntity<>(headers), ResponseString.class);

		ResponseString responseBody = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
	}

	@Test
	public void shouldAuthorizeUsingJwtAuthorizationHeader() {
		// Arrange
		String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);

		// Act
		ResponseEntity<ResponseString> response = restTemplate.exchange("/token", HttpMethod.POST,
				new HttpEntity<>(headers), ResponseString.class);

		ResponseString responseBody = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());
	}

	@Test
	public void shouldAddTokenCookieToResponse() {
		// Arrange & Act
		ResponseEntity<ResponseString> response =
				restTemplate.withBasicAuth(testUser.getUsername(), testUser.getPassword())
						.postForEntity("/token", null, ResponseString.class);

		HttpHeaders headers = response.getHeaders();
		String cookieHeader = headers.getFirst(HttpHeaders.SET_COOKIE);

		// Assert
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assertions.assertNotNull(cookieHeader);
		Assertions.assertTrue(cookieHeader.contains("token="));
	}
}
