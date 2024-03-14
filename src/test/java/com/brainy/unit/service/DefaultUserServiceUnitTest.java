package com.brainy.unit.service;

import static org.mockito.ArgumentMatchers.argThat;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

		// Act

		userService.registerUserFromRequest(request);

		// Assert

		Mockito.verify(userDao).registerUser(argThat(user -> user.getUsername().equals("test")));
	}

	private UserRegistrationRequest createMockUserRegistrationRequest() {
		UserRegistrationRequest request =
				new UserRegistrationRequest("test", "testPass1", "test@test.com", "test", "test");

		return request;
	}

	@Test
	public void shouldUpdateUsernameToLowerCaseWhenRegistering() throws BadRequestException {
		// Arrange

		UserRegistrationRequest request =
				new UserRegistrationRequest("TeSt", "testPass1", "test@test.com", "test", "test");

		// Act

		userService.registerUserFromRequest(request);

		// Assert

		Mockito.verify(userDao).registerUser(argThat(user -> user.getUsername().equals("test")));
	}

	@Test
	public void shouldUpdateEmailToLowerCaseWhenRegistering() throws BadRequestException {
		// Arrange

		UserRegistrationRequest request =
				new UserRegistrationRequest("test", "testPass1", "TEST@test.com", "test", "test");

		// Act

		userService.registerUserFromRequest(request);

		// Assert

		Mockito.verify(userDao)
				.registerUser(argThat(user -> user.getEmail().equals("test@test.com")));
	}

	@Test
	public void shouldReturnTrueOnValidToken() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		Instant tokenIssueDate = Instant.now();

		Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.minus(1, ChronoUnit.MINUTES));
		Mockito.when(userDao.findUserByUsername(user.getUsername())).thenReturn(user);

		// Act

		user.setLogoutDate(changeTimestamp);
		user.setPasswordChangeDate(changeTimestamp);

		boolean actual = userService.isTokenStillValidForUser(tokenIssueDate, user.getUsername());

		// Assert

		Assertions.assertTrue(actual);
	}

	@Test
	public void shouldReturnFalseOnOutdatedToken() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		Instant tokenIssueDate = Instant.now();

		Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.plus(1, ChronoUnit.MINUTES));
		Mockito.when(userDao.findUserByUsername(user.getUsername())).thenReturn(user);

		// Act

		user.setLogoutDate(changeTimestamp);
		user.setPasswordChangeDate(changeTimestamp);

		boolean actual = userService.isTokenStillValidForUser(tokenIssueDate, user.getUsername());

		// Assert

		Assertions.assertFalse(actual);
	}

	@Test
	public void shouldReturnFalseOnTokenFOrNonExistingUser() {
		// Arrange

		String username = TestUtils.generateRandomUsername();
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

		Mockito.verify(user).setLogoutDate(argThat(logoutDate -> {
			Instant now = Instant.now();

			return ChronoUnit.MINUTES.between(logoutDate.toInstant(), now) <= 1;
		}));

		Mockito.verify(userDao).saveUserChanges(user);
	}

	@Test
	public void shouldUpdateUserPassword() throws BadRequestException {
		// Arrange

		User user = Mockito.mock();

		Mockito.when(user.getPassword()).thenReturn("user password");
		Mockito.when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");

		// Act

		userService.updateUserPassword(user, "newStrongPassword");

		// Assert

		Mockito.verify(user).setPasswordChangeDate(argThat(logoutDate -> {
			Instant now = Instant.now();

			return ChronoUnit.MINUTES.between(logoutDate.toInstant(), now) <= 1;
		}));

		Mockito.verify(userDao).saveUserChanges(user);
	}

	@Test
	public void shouldNotSaveChangesWhenAnotherUserWithSameEmailExists() {
		// Arrange

		User user = TestUtils.generateRandomUser();
		User anotherUser = TestUtils.generateRandomUser();
		anotherUser.setEmail(user.getEmail());

		Mockito.when(userDao.findUserByEmail(user.getEmail())).thenReturn(anotherUser);

		// Act & Assert

		Assertions.assertThrowsExactly(BadRequestException.class, () -> {
			userService.saveUserChanges(user);
		});
	}

	@Test
	public void shouldUpdateEmailToLowerCaseWhenSavingUserChanges() throws BadRequestException {
		// Arrange

		User user = new User("test", "testPass1", "TEST@test.com", "test", "test");

		// Act

		userService.saveUserChanges(user);

		// Assert

		Mockito.verify(userDao).saveUserChanges(
				argThat(savedUser -> savedUser.getEmail().equals("test@test.com")));
	}
}
