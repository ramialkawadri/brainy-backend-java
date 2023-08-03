package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.IntegrationTest;
import com.brainy.integration.IntegrationTestUtils;
import com.brainy.integration.model.ResponseString;
import com.brainy.model.dto.UpdatePasswordDto;
import com.brainy.model.entity.User;

public class AuthControllerTest extends IntegrationTest {

    @Test
    public void shouldRegisterUser() {
        User tmpUser = IntegrationTestUtils.generateRandomUser();
        IntegrationTestUtils.registerUser(restTemplate, tmpUser);

        ResponseEntity<ResponseString> response = restTemplate
                .withBasicAuth(testUser.getUsername(), testUser.getPassword())
                .postForEntity("/token", null, ResponseString.class);
                
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldNotAuthorize() {
        ResponseEntity<Void> response =
                restTemplate.postForEntity("/token", null, Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldNotAuthorizeTokenAfterLogout() {
        String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        ResponseEntity<Void> logoutResponse = restTemplate
                .exchange("/logout", HttpMethod.POST, new HttpEntity<>(headers),
                        Void.class);

        Assertions.assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        ResponseEntity<Void> response = restTemplate
                .exchange("/token", HttpMethod.POST, new HttpEntity<>(headers),
                        Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldNotAuthorizeTokenAfterPasswordChange() {
        String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        UpdatePasswordDto body = new UpdatePasswordDto("StrongPass1");

        ResponseEntity<Void> logoutResponse = restTemplate
                .exchange("/password", HttpMethod.POST, 
                        new HttpEntity<>(body, headers),
                        Void.class);

        Assertions.assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());

        ResponseEntity<Void> response = restTemplate
                .exchange("/token", HttpMethod.POST, new HttpEntity<>(headers),
                        Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void shouldAuthorizeUsingHeaders() {
        ResponseEntity<ResponseString> response = restTemplate
                .withBasicAuth(testUser.getUsername(), testUser.getPassword())
                .postForEntity("/token", null, ResponseString.class);
                
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getData());
    }

    @Test
    public void shouldAuthorizeUsingJwtCookies() {
        String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=" + token);

        ResponseEntity<ResponseString> response = restTemplate
                .exchange("/token", HttpMethod.POST, new HttpEntity<>(headers),
                        ResponseString.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getData());
    }

    @Test
    public void shouldAuthorizeUsingJwtAuthorizationHeader() {
        String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        ResponseEntity<ResponseString> response = restTemplate
                .exchange("/token", HttpMethod.POST, new HttpEntity<>(headers),
                        ResponseString.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseString responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.getData());
    }

    @Test
    public void shouldAddTokenCookieToResponse() {
        ResponseEntity<ResponseString> response = restTemplate
                .withBasicAuth(testUser.getUsername(), testUser.getPassword())
                .postForEntity("/token", null, ResponseString.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        HttpHeaders headers = response.getHeaders();

        String cookieHeader = headers.getFirst(HttpHeaders.SET_COOKIE);

        Assertions.assertNotNull(cookieHeader);
        Assertions.assertTrue(cookieHeader.contains("token="));
    }
}
