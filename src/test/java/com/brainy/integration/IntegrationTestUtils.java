package com.brainy.integration;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.model.wrapper.ResponseString;
import com.brainy.model.entity.User;
import com.brainy.model.request.UserRegistrationRequest;

public class IntegrationTestUtils {

	public static void registerUser(TestRestTemplate restTemplate, User user) {
		UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.fromUser(user);
		HttpEntity<UserRegistrationRequest> request = new HttpEntity<>(userRegistrationRequest);

		ResponseEntity<Void> response =
				restTemplate.postForEntity("/register", request, Void.class);

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	public static String getAccessToken(TestRestTemplate restTemplate, User user) {
		ResponseEntity<ResponseString> response =
				restTemplate.withBasicAuth(user.getUsername(), user.getPassword())
						.postForEntity("/token", null, ResponseString.class);

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		ResponseString responseBody = response.getBody();
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());

		return responseBody.getData();
	}

	/**
	 * @param restTemplate must be authenticated
	 */
	public static String uploadFile(TestRestTemplate restTemplate, String filename,
			String fileContent) {

		ResponseEntity<ResponseString> response =
				restTemplate.postForEntity("/api/files?filename=" + filename,
						new HttpEntity<>(fileContent), ResponseString.class);

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		ResponseString responseBody = response.getBody();
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());

		return responseBody.getData();
	}

	/**
	 * @param restTemplate must be authenticated
	 */
	public static String getFileContent(TestRestTemplate restTemplate, String filename) {
		ResponseEntity<ResponseString> response =
				restTemplate.getForEntity("/api/files?filename=" + filename, ResponseString.class);

		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

		ResponseString responseBody = response.getBody();
		Assertions.assertNotNull(responseBody);
		Assertions.assertNotNull(responseBody.getData());

		return responseBody.getData();
	}
}
