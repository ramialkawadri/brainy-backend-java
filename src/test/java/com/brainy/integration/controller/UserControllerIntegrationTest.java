package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.IntegrationTest;
import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.integration.model.wrapper.ResponseUser;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdateUserRequest;

public class UserControllerIntegrationTest extends IntegrationTest {

	@Test
	public void shouldGetUserInformation() {
		// Arrange & Act

		ResponseEntity<ResponseUser> response = getUserInformation();
		ResponseUser body = response.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		Assertions.assertNotNull(body);
		User responseUser = body.getData();

		Assertions.assertEquals(testUser.getUsername(), responseUser.getUsername());
		Assertions.assertEquals(testUser.getEmail(), responseUser.getEmail());
	}

	@Test
	public void shouldUpdateUser() {
		// Arrange

		HttpEntity<UpdateUserRequest> updaterUserRequestBody =
				new HttpEntity<>(new UpdateUserRequest("new", null, null));

		// Act

		getAuthenticatedRequest().patchForObject("/api/user", updaterUserRequestBody,
				ResponseString.class);

		ResponseEntity<ResponseUser> userInformationResponse = getUserInformation();

		ResponseUser body = userInformationResponse.getBody();

		// Assert

		Assertions.assertEquals(HttpStatus.OK, userInformationResponse.getStatusCode());
		Assertions.assertNotNull(body);
		Assertions.assertEquals(body.getData().getFirstName(), "new");
	}

	private ResponseEntity<ResponseUser> getUserInformation() {
		return getAuthenticatedRequest().getForEntity("/api/user", ResponseUser.class);
	}
}
