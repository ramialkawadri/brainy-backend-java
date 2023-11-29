package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.brainy.integration.IntegrationTest;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.model.ResponseStatus;
import com.brainy.model.request.UserRegistrationRequest;

public class GlobalExceptionHandlerIntegrationTest extends IntegrationTest {

	@Test
	public void shouldHandleRequestException() {
		// Arrange
		UserRegistrationRequest userRegistrationRequest =
				UserRegistrationRequest.fromUser(testUser);
		HttpEntity<UserRegistrationRequest> request = new HttpEntity<>(userRegistrationRequest);

		// Act
		ResponseEntity<ResponseString> response =
				restTemplate.postForEntity("/register", request, ResponseString.class);
		ResponseString body = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(ResponseStatus.BAD_REQUEST, body.getStatus());
		Assertions.assertTrue(
				body.getData().startsWith(ResponseStatus.BAD_REQUEST.getStatusString()));
	}

	@Test
	public void shouldHandleEndpointNotFoundException() {
		// Act
		ResponseEntity<ResponseString> response =
				getAuthenticatedRequest().getForEntity("/someRandomString", ResponseString.class);
		ResponseString body = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(ResponseStatus.NOT_FOUND, body.getStatus());
	}

	@Test
	public void shouldHandleMissingBody() {
		// Act
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> httpEntity = new HttpEntity<>(headers);

		// POST /api/user is used because it requires a body for the updated user
		ResponseEntity<ResponseString> response = getAuthenticatedRequest()
				.postForEntity("/api/user", httpEntity, ResponseString.class);
		ResponseString body = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(ResponseStatus.BAD_REQUEST, body.getStatus());
	}

	@Test
	public void shouldHandleMissingParameter() {
		// Arrange
		String expectedBodyData = "the query string 'filename' is missing!";

		// Act
		HttpEntity<String> httpEntity = new HttpEntity<>("Body");

		// POST /api/files requires a parameter
		ResponseEntity<ResponseString> response = getAuthenticatedRequest()
				.postForEntity("/api/files", httpEntity, ResponseString.class);
		ResponseString body = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(ResponseStatus.BAD_REQUEST, body.getStatus());
		Assertions.assertEquals(expectedBodyData, body.getData());
	}

	@Test
	public void shouldHandleUnknownError() {
		// Act

		// This will result in unknown content type for the system so it throws an internal server
		// error
		HttpEntity<?> httpEntity = new HttpEntity<>(null);

		// POST /api/user is used because it requires a body
		ResponseEntity<ResponseString> response = getAuthenticatedRequest()
				.postForEntity("/api/user", httpEntity, ResponseString.class);
		ResponseString body = response.getBody();

		// Assert
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(ResponseStatus.ERROR, body.getStatus());
	}
}