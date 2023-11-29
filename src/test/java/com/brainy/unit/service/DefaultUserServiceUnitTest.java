package com.brainy.unit.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.brainy.TestUtils;
import com.brainy.dao.UserDao;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UserRegistrationRequest;
import com.brainy.service.DefaultUserService;

public class DefaultUserServiceUnitTest {

	private UserDao userDao;
	private DefaultUserService userService;
	private PasswordEncoder passwordEncoder;

	public DefaultUserServiceUnitTest() {
		userDao = Mockito.mock();
		passwordEncoder = Mockito.mock();
		userService = new DefaultUserService(userDao, passwordEncoder);
	}

	@Test
	public void shouldGetUserByUsername() {
		// Arrange
		User mockUser = TestUtils.generateRandomUser();

		Mockito.when(userDao.findUserByUsername("user")).thenReturn(mockUser);

		// Act
		User user = userService.findUserByUsername("user");

		// Assert
		Mockito.verify(userDao).findUserByUsername("user");
		Assertions.assertEquals(mockUser, user);
	}

	@Test
	public void shouldRegisterUser() throws BadRequestException {
		// Arrange
		UserRegistrationRequest request = createMockUserRegistrationRequest();

		Mockito.doAnswer(invocation -> {
			User user = (User) invocation.getArguments()[0];

			Assertions.assertNotNull(user);
			Assertions.assertNotEquals("testPass", user.getPassword());

			return true;
		}).when(userDao).registerUser(Mockito.any());

		// Act & Assert
		userService.registerUserFromRequest(request);
	}

	private UserRegistrationRequest createMockUserRegistrationRequest() {
		UserRegistrationRequest request =
				new UserRegistrationRequest("test", "testPass1", "test@test.com", "test", "test");

		return request;
	}

	@Test
	public void shouldReturnTrueOnValidToken() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		Instant tokenIssueDate = Instant.now();

		Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.minus(1, ChronoUnit.MINUTES));
		Mockito.when(userDao.findUserByUsername(Mockito.anyString())).thenReturn(user);

		// Act
		user.setLogoutDate(changeTimestamp);
		user.setPasswordChangeDate(changeTimestamp);

		// Assert
		Assertions.assertTrue(userService.isTokenStillValidForUser(tokenIssueDate, ""));
	}

	@Test
	public void shouldReturnFalseOnOutdatedToken() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		Instant tokenIssueDate = Instant.now();

		Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.plus(1, ChronoUnit.MINUTES));
		Mockito.when(userDao.findUserByUsername(Mockito.anyString())).thenReturn(user);

		// Act
		user.setLogoutDate(changeTimestamp);
		user.setPasswordChangeDate(changeTimestamp);

		// Assert
		Assertions.assertFalse(userService.isTokenStillValidForUser(tokenIssueDate, ""));
	}

	@Test
	public void shouldReturnFalseOnTokenFOrNonExistingUser() {
		// Arrange
		String username = TestUtils.generateUniqueUsername();
		Mockito.when(userDao.findUserByUsername(username)).thenReturn(null);

		// Act
		boolean actual = userService.isTokenStillValidForUser(Instant.now(), username);

		// Assert
		Assertions.assertFalse(actual);
	}

	@Test
	public void shouldLogoutUser() {
		// Arrange
		User user = Mockito.mock();

		// Act
		userService.logoutUser(user);

		// Assert
		Mockito.verify(user).setLogoutDate(Mockito.any());
		Mockito.verify(userDao).saveUserChanges(user);
	}

	@Test
	public void shouldUpdateUserPassword() throws BadRequestException {
		// Arrange
		User user = Mockito.mock();

		Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded");

		// Act
		userService.updateUserPassword(user, "newStrongPassword");

		// Assert
		Mockito.verify(user).setPasswordChangeDate(Mockito.any());
		Mockito.verify(userDao).saveUserChanges(user);
	}

	@Test
	public void shouldNotSaveUserCHangesWhenInvalid() {
		// Arrange
		User user = TestUtils.generateRandomUser();
		Mockito.doAnswer(invocation -> {
			throw new DataIntegrityViolationException("");
		}).when(userDao).saveUserChanges(user);

		// Act & Assert
		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userService.saveUserChanges(user);
		});
	}
}
