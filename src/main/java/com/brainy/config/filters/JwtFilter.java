package com.brainy.config.filters;

import java.io.IOException;

import com.brainy.entity.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class JwtFilter implements Filter {

    @Override
    public void doFilter(
        ServletRequest servletRequest, 
        ServletResponse servletResponse, 
        FilterChain chain) throws IOException, ServletException {
        
        User user = (User) servletRequest.getAttribute("user");

        if (user != null) {
            // TODO: check if the token issued date is after last log out time and after last password change time
        }

        chain.doFilter(servletRequest, servletResponse);
    }
    
}
