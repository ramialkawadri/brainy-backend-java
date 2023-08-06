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
        User tmpUser = TestUtils.generateRandomUser();
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

        UpdatePasswordRequest body = new UpdatePasswordRequest("StrongPass1");

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
    public void shouldNotAuthorizeBecauseOfBadToken() {
        String token = IntegrationTestUtils.getAccessToken(restTemplate, testUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "token=" + token + "RANDOM_STRING");

        ResponseEntity<Void> response = restTemplate.exchange(
                "/token", HttpMethod.POST, new HttpEntity<>(headers), Void.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
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
