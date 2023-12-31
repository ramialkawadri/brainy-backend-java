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
	public void shouldNotAcceptShortUsername() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("aa", "StrongPassword1",
				"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptLongUsername() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("a".repeat(64),
				"StrongPassword1", "test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptUsernameThatDoesNotStartWithLetterOrANumber() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("-username", "StrongPassword1",
				"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptUsernameThatDoesNotEndWithLetterOrNumber() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("username-", "StrongPassword1",
				"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptUsernameThatContainsInvalidCharacters() {
		// Arrange
		UserRegistrationRequest user = new UserRegistrationRequest("user name", "StrongPassword1",
				"test@test.com", "firstName", "lastName");

		// Act
		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// Assert
		Assertions.assertEquals(1, violations.size());
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
		Assertions.assertTrue(violations.size() >= 1);
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
