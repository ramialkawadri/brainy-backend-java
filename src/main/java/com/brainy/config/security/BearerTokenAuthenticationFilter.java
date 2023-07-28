package com.brainy.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brainy.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private UserDetailsManager userDetailsManager;

    public BearerTokenAuthenticationFilter(
            TokenService tokenService,
            UserDetailsManager userDetailsManager) {

        this.tokenService = tokenService;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            FilterChain chain) throws ServletException, IOException {

        Jwt token = getJwtFromRequest(servletRequest);

        useTokenForAuthenticationIfValid(token);

        chain.doFilter(servletRequest, servletResponse);
    }

    private Jwt getJwtFromRequest(HttpServletRequest request) {
        String encodedToken;

        if (isJwtInCookies(request))
            encodedToken = getJwtFromCookies(request);
        else
            encodedToken = getJwtFromAuthorizationHeader(request);

        return (encodedToken == null || encodedToken.isEmpty())
                ? null
                : tokenService.decodeToken(encodedToken);
    }

    private void useTokenForAuthenticationIfValid(Jwt jwt) {
        if (jwt == null || tokenService.isTokenExpired(jwt))
            return;

        // TODO: check if user token is issued after password change and log out

        setAuthenticationFromToken(jwt);
    }

    private void setAuthenticationFromToken(Jwt jwt) {
        String username = jwt.getSubject();

        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private boolean isJwtInCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return false;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token"))
                return true;
        }

        return false;
    }

    private String getJwtFromAuthorizationHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            return accessToken;
        }

        return null;
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token"))
                return cookie.getValue();
        }

        return null;
    }

}
