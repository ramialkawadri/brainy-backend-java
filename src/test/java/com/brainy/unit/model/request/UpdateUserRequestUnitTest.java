package com.brainy.unit.model.request;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.brainy.TestUtils;
import com.brainy.model.entity.User;
import com.brainy.model.request.UpdateUserRequest;
import com.brainy.unit.UnitTestUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class UpdateUserRequestUnitTest {

	private Validator validator;

	public UpdateUserRequestUnitTest() {
		validator = UnitTestUtils.getValidator();
	}

	@Test
	public void shouldNotAcceptUpdate() {
		// Arrange

		UpdateUserRequest request = new UpdateUserRequest("s", null, "email.com");

		// Act

		Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

		// Assert

		Assertions.assertEquals(2, violations.size());
	}

	@Test
	public void shouldApplyUpdatesOnUser() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		UpdateUserRequest updateUserRequest =
				new UpdateUserRequest("new first name", "new last name", "new email");

		// Act

		updateUserRequest.applyUpdatesOnUser(user);

		// Assert

		Assertions.assertEquals(updateUserRequest.firstName(), user.getFirstName());
		Assertions.assertEquals(updateUserRequest.lastName(), user.getLastName());
		Assertions.assertEquals(updateUserRequest.email(), user.getEmail());
	}
}
