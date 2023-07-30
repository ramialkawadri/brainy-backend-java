package com.brainy.integration.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.controller.UserController;
import com.brainy.integration.IntegrationTest;
import com.brainy.model.Response;
import com.brainy.model.entity.User;

public class UserControllerTest extends IntegrationTest {

    private UserController userController;

    public UserControllerTest() {
        userController = new UserController();
    }

    @Test
    public void shouldGetUserInformation() {
        User testUser = new User();
        Response<User> response = userController.getUserInformation(testUser);

        Assertions.assertEquals(testUser, response.getData());
    }
}
