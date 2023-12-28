package com.brainy.model.request;

import com.brainy.model.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
		@JsonProperty("first-name") @Size(min = 2,
				message = "fill the first name") String firstName,
		@JsonProperty("last-name") @Size(min = 2, message = "fill the last name") String lastName,
		@Email(message = "invalid email") String email) {

	public void applyUpdatesOnUser(User user) {
		if (firstName != null)
			user.setFirstName(firstName);

		if (lastName != null)
			user.setLastName(lastName);

		if (email != null)
			user.setEmail(email);
	}
}
