package com.brainy.unit.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.TestUtils;
import com.brainy.controller.AuthController;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdatePasswordRequest;
import com.brainy.model.request.UserRegistrationRequest;
import com.brainy.service.TokenService;
import com.brainy.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class AuthControllerUnitTest {

    private TokenService tokenService;
    private UserService userService;
    private AuthController authController;

    public AuthControllerUnitTest() {
        tokenService = Mockito.mock();
        userService = Mockito.mock();
        authController = new AuthController(tokenService, userService);
    }

    @Test
    public void shouldGetTokenAndAddItToCookies() {
        HttpServletResponse servletResponse = Mockito.mock();

        Mockito.when(tokenService.generateToken(Mockito.any()))
                .thenReturn("token_value");

        Mockito.doAnswer(invocation -> {
            Cookie cookie = (Cookie) invocation.getArguments()[0];

            Assertions.assertEquals("token", cookie.getName());
            Assertions.assertEquals("token_value", cookie.getValue());

            return true;
        }).when(servletResponse).addCookie(Mockito.any());

        Response<String> tokenResponse = authController.getToken(null, servletResponse);

        Assertions.assertEquals(ResponseStatus.SUCCESS, tokenResponse.getStatus());
        Assertions.assertEquals("token_value", tokenResponse.getData());
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test",
                "StrongPass1",
                "test@test.com",
                "firstName",
                "lastName");

        authController.registerUser(request);

        Mockito.verify(userService).registerUserFromRequest(request);
    }

    @Test
    public void shouldLogout() {
        User user = TestUtils.generateRandomUser();
        authController.logout(user);
        Mockito.verify(userService).logoutUser(user);
    }

    @Test
    public void shouldChangePassword() throws BadRequestException {
        User user = TestUtils.generateRandomUser();
        UpdatePasswordRequest request = new UpdatePasswordRequest("StrongPass1");

        authController.changeUserPassword(user, request);

        Mockito.verify(userService).updateUserPassword(user, "StrongPass1");
    }
}
