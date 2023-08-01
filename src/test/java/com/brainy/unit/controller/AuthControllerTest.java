package com.brainy.unit.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.brainy.controller.AuthController;
import com.brainy.exception.BadRequestException;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;
import com.brainy.service.TokenService;
import com.brainy.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class AuthControllerTest {

    private TokenService tokenService;
    private UserService userService;
    private AuthController authController;

    public AuthControllerTest() {
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

        Response<String> tokenResponse 
                = authController.getToken(null, servletResponse);
        
        Assertions.assertEquals(ResponseStatus.SUCCESS, tokenResponse.getStatus());
        Assertions.assertEquals("token_value", tokenResponse.getData());
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "test");
        requestBody.put("password", "test");
        requestBody.put("email", "test@test.com");
        requestBody.put("firstName", "test");
        requestBody.put("lastName", "test");

        authController.registerUser(requestBody);

        Mockito.verify(userService).registerUserFromRequest(requestBody);
    }

    @Test
    public void shouldLogout() {
        User user = new User();
        authController.logout(user);
        Mockito.verify(userService).logoutUser(user);
    }

    @Test
    public void shouldChangePassword() throws BadRequestException {
        User user = new User();
        Map<String, String> requestBody = Mockito.mock();

        Mockito.when(requestBody.get("newPassword")).thenReturn("new password");

        authController.changeUserPassword(user, requestBody);

        Mockito.verify(userService).updateUserPassword(user, "new password");
    }

    @Test
    public void shouldNotChangePassword() {
        User user = new User();
        Map<String, String> requestBody = Mockito.mock();

        Mockito.when(requestBody.get("newPassword")).thenReturn(null);

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            authController.changeUserPassword(user, requestBody);
        });
    }
}
