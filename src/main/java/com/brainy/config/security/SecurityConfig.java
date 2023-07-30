package com.brainy.config.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    public SecurityConfig(
            BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter) {
        this.bearerTokenAuthenticationFilter = bearerTokenAuthenticationFilter;
    }

    @Bean
    PasswordEncoder defaultPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityConfiguration(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        authorizeHttpRequests(http);

        http.addFilterBefore(
                bearerTokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

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
                .requestMatchers(HttpMethod.POST, "/register/**").permitAll());
    }
}
