package com.brainy.config.security;

import java.io.IOException;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brainy.service.TokenService;
import com.brainy.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private UserService userService;
    private UserDetailsManager userDetailsManager;

    public BearerTokenAuthenticationFilter(
            TokenService tokenService,
            UserService userService,
            UserDetailsManager userDetailsManager) {

        this.tokenService = tokenService;
        this.userService = userService;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            FilterChain chain) throws ServletException, IOException {

        try {
            Jwt token = getJwtFromRequest(servletRequest);
            useTokenForAuthenticationIfValid(token);
        } catch (BadJwtException ignored) {
        } finally {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Nullable
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

    private void useTokenForAuthenticationIfValid(@Nullable Jwt jwt) {
        if (jwt == null || tokenService.isTokenExpired(jwt) ||
                !userService.isTokenStillValidForUser(
                        jwt.getIssuedAt(), jwt.getSubject()))
            return;

        setAuthenticationFromToken(jwt);
    }

    private void setAuthenticationFromToken(Jwt jwt) {
        String username = jwt.getSubject();

        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
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

    @Nullable
    private String getJwtFromAuthorizationHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            return accessToken;
        }

        return null;
    }

    @Nullable
    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token"))
                return cookie.getValue();
        }

        return null;
    }

}
