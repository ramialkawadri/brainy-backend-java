package com.brainy.unit.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.controller.UserController;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;

public class UserControllerTest {
    
    private UserController userController;

    public UserControllerTest() {
        userController = new UserController();
    }

    @Test
    public void shouldReturnUserInformation() {
        User user = new User("test", "test", "test@test.com", "test", "test");

        Response<User> response = userController.getUserInformation(user);

        Assertions.assertEquals(ResponseStatus.SUCCESS, response.getStatus());
        Assertions.assertEquals(user, response.getData());
    }
}
