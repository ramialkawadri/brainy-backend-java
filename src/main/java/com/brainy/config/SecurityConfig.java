package com.brainy.config;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // TODO: write tests for authentication

    @Value("${jwt.key}")
    private String jwtKey;

    @Bean
    UserDetailsManager databaseUserDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = 
                new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select username, password, true from users where username=?"
        );

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select ?, 'user'"
        );

        return jdbcUserDetailsManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain authorizeRoutes(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(authorize ->
        authorize
            .requestMatchers("/api/**").authenticated()
            .requestMatchers("/token").authenticated()
            .requestMatchers("/").permitAll()
            .requestMatchers("/register").permitAll()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                Customizer.withDefaults()
        ));

        http.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey.getBytes()));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        byte[] bytes = jwtKey.getBytes();

        SecretKeySpec originalKey = 
                new SecretKeySpec(bytes, 0, bytes.length, "RSA");

        return NimbusJwtDecoder.withSecretKey(originalKey)
                .macAlgorithm(MacAlgorithm.HS512).build();
    }
}
