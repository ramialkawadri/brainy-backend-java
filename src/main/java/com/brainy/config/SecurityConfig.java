package com.brainy.config;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.brainy.config.filters.JwtFilter;
import com.brainy.config.filters.UserFilter;
import com.brainy.service.TokenService;
import com.brainy.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserService userService;
    private TokenService tokenService;
    private UserDetailsManager userDetailsManager;

    public SecurityConfig(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Bean
    UserDetailsManager databaseUserDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = 
                new JdbcUserDetailsManager(dataSource);

        this.userDetailsManager = jdbcUserDetailsManager;

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select username, password, true from users where username=?");

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select ?, 'user'");

        return jdbcUserDetailsManager;
    }

    @Bean
    PasswordEncoder defaultPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityConfiguration(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        authorizeHttpRequests(http);

        addFilters(http);

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.httpBasic(withDefaults());

        return http.build();
    }

    private void authorizeHttpRequests(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/token").authenticated()
                .requestMatchers("/").permitAll()
                .requestMatchers("/register").permitAll());
    }

    private void addFilters(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenService, userDetailsManager),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterAfter(
                new UserFilter(userService), AuthorizationFilter.class);
    } 
}
