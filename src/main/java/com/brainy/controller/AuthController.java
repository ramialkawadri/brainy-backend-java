package com.brainy.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.exception.BadRequestException;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;
import com.brainy.service.TokenService;
import com.brainy.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public void registerUser(
            @RequestBody(required = false) Map<String, String> requestBody)
            throws BadRequestException {

        if (requestBody == null)
            throw new BadRequestException("missing body");

        userService.registerUserFromRequest(requestBody);
    }

    @PostMapping("/logout")
    public void logout(@RequestAttribute User user) {
        userService.logoutUser(user);
    }

    @PostMapping("/password")
    public void changeUserPassword(
            @RequestAttribute User user,
            @RequestBody(required = false) Map<String, String> requestBody)
            throws BadRequestException {

        String newPasswordPropertyName = "newPassword";

        if (requestBody == null || 
                requestBody.get(newPasswordPropertyName) == null)
            throw new BadRequestException(
                    "please provide a body with the new password!");

        userService.updateUserPassword(user,
                requestBody.get(newPasswordPropertyName));
    }

    @PostMapping("/token")
    public Response<String> getToken(
            @RequestAttribute User user,
            HttpServletResponse response) {

        String jwt = tokenService.generateToken(user);
        addJwtCookieToResponse(response, jwt);
        return new Response<String>(jwt, ResponseStatus.SUCCESS);
    }

    private void addJwtCookieToResponse(
            HttpServletResponse response, String token) {

        Cookie jwtCookie = new Cookie("token", token);
        response.addCookie(jwtCookie);
    }
}
