package com.brainy.config.filters;

import java.io.IOException;
import java.security.Principal;

import com.brainy.entity.User;
import com.brainy.service.UserService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class UserFilter implements Filter {

    private UserService userService;

    public UserFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(
        ServletRequest servletRequest, 
        ServletResponse servletResponse, 
        FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            User user = userService.getUserByUsername(principal.getName());
            servletRequest.setAttribute("user", user);
        }

        chain.doFilter(servletRequest, servletResponse);
    }
    
}
