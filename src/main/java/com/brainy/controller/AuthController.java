package com.brainy.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
            Authentication authentication, HttpServletResponse response) {

        String jwt = tokenService.generateToken(authentication);
        addJwtCookieToResponse(response, jwt);
        return new Response<String>(jwt, ResponseStatus.SUCCESS);
    }

    private void addJwtCookieToResponse(
            HttpServletResponse response, String token) {

        Cookie jwtCookie = new Cookie("token", token);
        response.addCookie(jwtCookie);
    }
}
