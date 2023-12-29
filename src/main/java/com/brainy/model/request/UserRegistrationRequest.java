package com.brainy.model.request;

import com.brainy.model.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Used to represent the object for registering the user!
 */
public record UserRegistrationRequest(
		@NotBlank(message = "missing") @Size(min = 3,
				message = "minimum length of username is 3") String username,

		@NotBlank(message = "missing") @Size(min = 8,
				message = "must be at least 8 characters") @Pattern(
						regexp = User.userPasswordValidationRegExpr,
						message = "must contain one small letter, capital letter and a number") String password,

		@NotBlank(message = "missing") @Email(message = "invalid") String email,
		@JsonProperty("first-name") @NotBlank(message = "missing") String firstName,
		@JsonProperty("last-name") @NotBlank(message = "missing") String lastName) {

	public User toUser() {
		return new User(username(), password(), email(), firstName(), lastName());
	}

	public static UserRegistrationRequest fromUser(User user) {
		return new UserRegistrationRequest(user.getUsername(), user.getPassword(), user.getEmail(),
				user.getFirstName(), user.getPassword());
	}
}
