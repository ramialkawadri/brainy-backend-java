package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.brainy.integration.IntegrationTest;
import com.brainy.integration.model.wrapper.ResponseUser;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdateUserRequest;

public class UserControllerIntegrationTest extends IntegrationTest {

    @Test
    public void shouldGetUserInformation() {
        ResponseEntity<ResponseUser> response = getUserInformation();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseUser body = response.getBody();

        Assertions.assertNotNull(body);

        User responseUser = body.getData();
        Assertions.assertEquals(testUser.getUsername(), responseUser.getUsername());
        Assertions.assertEquals(testUser.getEmail(), responseUser.getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        HttpEntity<UpdateUserRequest> request = new HttpEntity<>(new UpdateUserRequest("new", null, null));

        ResponseEntity<Void> updateResponse = authenticatedRequest()
                .postForEntity("/api/user", request, Void.class);

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<ResponseUser> userInformation = getUserInformation();

        Assertions.assertEquals(HttpStatus.OK, userInformation.getStatusCode());

        ResponseUser body = userInformation.getBody();

        Assertions.assertNotNull(body);

        Assertions.assertEquals(body.getData().getFirstName(), "new");
    }

    private ResponseEntity<ResponseUser> getUserInformation() {
        return authenticatedRequest()
                .getForEntity("/api/user", ResponseUser.class);
    }
}
