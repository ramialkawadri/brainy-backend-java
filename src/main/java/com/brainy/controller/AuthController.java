package com.brainy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid UserRegistrationRequest request)
            throws BadRequestException {

        userService.registerUserFromRequest(request);
    }

    @PostMapping("/logout")
    public void logout(@RequestAttribute User user) {
        userService.logoutUser(user);
    }

    @PostMapping("/password")
    public void changeUserPassword(
            @RequestAttribute User user,
            @RequestBody @Valid UpdatePasswordRequest request) {

        userService.updateUserPassword(user, request.password());
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
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setAttribute("SameSite", "Strict");
        jwtCookie.setSecure(true);
        response.addCookie(jwtCookie);
    }
}
