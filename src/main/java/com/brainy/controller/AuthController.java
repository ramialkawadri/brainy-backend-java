package com.brainy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.entity.User;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.service.TokenService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
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
