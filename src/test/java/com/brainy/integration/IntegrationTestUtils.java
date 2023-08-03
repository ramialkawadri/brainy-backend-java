package com.brainy.integration;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.model.ResponseString;
import com.brainy.model.dto.UserRegistrationDto;
import com.brainy.model.entity.User;

public class IntegrationTestUtils {

    public static User generateRandomUser() {
        String username = UUID.randomUUID().toString();

        return new User(
                username,
                "testPassword123",
                username + "@test.com",
                "firstName_" + username,
                "lastName_" + username);
    }

    public static void registerUser(TestRestTemplate restTemplate, User user) {
        UserRegistrationDto dto = UserRegistrationDto.fromUser(user);

        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(dto);

        ResponseEntity<Void> response = 
                restTemplate.postForEntity("/register", request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    public static String getAccessToken(TestRestTemplate restTemplate, User user) {
        ResponseEntity<ResponseString> response = restTemplate
                .withBasicAuth(user.getUsername(), user.getPassword())
                .postForEntity("/token", null, ResponseString.class);
                
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getData());

        return responseBody.getData();
    }

    public static TestRestTemplate getAuthenticatedTemplate(
        TestRestTemplate restTemplate, User user) {
        
        return restTemplate.withBasicAuth(user.getUsername(), user.getPassword());
    }
}
