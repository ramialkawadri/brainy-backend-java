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
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateUniqueUsername(), "StrongPassword1",
						"test@test.com", "firstName", "lastName");

		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		Assertions.assertEquals(0, violations.size());
	}

	@Test
	public void shouldNotAcceptBlanks() {
		UserRegistrationRequest user = new UserRegistrationRequest("", "", "", "", "");

		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		Assertions.assertFalse(violations.isEmpty());
	}

	@Test
	public void shouldNotAcceptInvalidEmail() {
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateUniqueUsername(), "StrongPassword1",
						"test@.com", "firstName", "lastName");

		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		Assertions.assertEquals(1, violations.size());
	}

	@Test
	public void shouldNotAcceptShortPassword() {
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateUniqueUsername(), "Pass1",
						"test@test.com", "firstName", "lastName");

		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		// 2 Because of the regular expression also counts
		Assertions.assertEquals(2, violations.size());
	}

	@Test
	public void shouldNotAcceptWeekPassword() {
		UserRegistrationRequest user =
				new UserRegistrationRequest(TestUtils.generateUniqueUsername(), "password",
						"test@test.com", "firstName", "lastName");

		Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(user);

		Assertions.assertEquals(1, violations.size());
	}
}
