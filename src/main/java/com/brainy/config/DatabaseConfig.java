package com.brainy.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class DatabaseConfig {

	@Bean
	UserDetailsManager databaseUserDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		// All usernames must be in lower case
		jdbcUserDetailsManager.setUsersByUsernameQuery(
				"select username, password, true from users where username=LOWER(?)");

		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select LOWER(?), 'user'");

		return jdbcUserDetailsManager;
	}
}
