package com.brainy.unit.model.request;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.TestUtils;
import com.brainy.model.request.UserRegistrationRequest;
import com.brainy.unit.UnitTestUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class UserRegistrationRequestUnitTest {

	private Validator validator;

	public UserRegistrationRequestUnitTest() {
		validator = UnitTestUtils.getValidator();
	}

	@Test
	public void shouldAcceptValidValues() {
		// Arrange
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateRandomUsername(), "StrongPassword1",
						"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(0, violations.size());
	}

	@Test
	public void shouldNotAcceptBlanks() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("", "", "", "", "");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertFalse(violations.isEmpty());
	}

	@Test
	public void shouldNotAcceptInvalidEmail() {
		// Arrange
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateRandomUsername(), "StrongPassword1",
						"test@.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptShortPassword() {
		// Arrange
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateRandomUsername(), "Pass1",
						"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		// 2 Because of the regular expression also counts
		Assertions.assertEquals(2, violations.size());
	}

	@Test
	public void shouldNotAcceptWeekPassword() {
		// Arrange
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateRandomUsername(), "password",
						"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}
}
