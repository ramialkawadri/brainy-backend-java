package com.brainy.integration;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.model.ResponseString;
import com.brainy.model.entity.User;

public class IntegrationTestUtils {

    public static void registerUser(TestRestTemplate restTemplate, User user) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", user.getUsername());
        requestBody.put("password", user.getPassword());
        requestBody.put("email", user.getEmail());
        requestBody.put("firstName", user.getFirstName());
        requestBody.put("lastName", user.getLastName());

        HttpEntity<Map<String,String>> request = new HttpEntity<>(requestBody);

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
}
