package com.brainy.config;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.brainy.model.entity.User;
import com.brainy.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class adds a User object of the authenticated user to the request so
 * controllers can access the object without having to do any work.
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    private UserService userService;

    public UserInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            User user = userService.findUserByUsername(principal.getName());
            request.setAttribute("user", user);
        }

        return true;
    }

}
