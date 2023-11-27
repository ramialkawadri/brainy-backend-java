package com.brainy.unit.service;

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
        User mockUser = TestUtils.generateRandomUser();

        Mockito.when(userDao.findUserByUserName("user")).thenReturn(mockUser);

        User user = userService.findUserByUsername("user");

        Mockito.verify(userDao).findUserByUserName("user");

        Assertions.assertEquals(mockUser, user);
    }

    @Test
    public void shouldRegisterUser() throws BadRequestException {
        UserRegistrationRequest request = createMockUserRegistrationRequest();

        Mockito.doAnswer(invocation -> {
            User user = (User) invocation.getArguments()[0];

            Assertions.assertNotNull(user);
            Assertions.assertNotEquals("testPass", user.getPassword());

            return true;
        }).when(userDao).registerUser(Mockito.any());

        userService.registerUserFromRequest(request);
    }

    private UserRegistrationRequest createMockUserRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test",
                "testPass1",
                "test@test.com",
                "test",
                "test");

        return request;
    }

    @Test
    public void shouldReturnTrueOnValidToken() {
        User user = TestUtils.generateRandomUser();
        Instant tokenIssueDate = Instant.now();

        Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.minus(1, ChronoUnit.MINUTES));

        user.setLogoutDate(changeTimestamp);
        user.setPasswordChangeDate(changeTimestamp);

        Mockito.when(userDao.findUserByUserName(Mockito.anyString()))
                .thenReturn(user);

        Assertions.assertTrue(userService
                .isTokenStillValidForUser(tokenIssueDate, ""));
    }

    @Test
    public void shouldReturnFalseOnValidToken() {
        User user = TestUtils.generateRandomUser();
        Instant tokenIssueDate = Instant.now();

        Timestamp changeTimestamp = Timestamp.from(tokenIssueDate.plus(1, ChronoUnit.MINUTES));

        user.setLogoutDate(changeTimestamp);
        user.setPasswordChangeDate(changeTimestamp);

        Mockito.when(userDao.findUserByUserName(Mockito.anyString()))
                .thenReturn(user);

        Assertions.assertFalse(userService
                .isTokenStillValidForUser(tokenIssueDate, ""));
    }

    @Test
    public void shouldLogoutUser() {
        User user = Mockito.mock();

        userService.logoutUser(user);

        Mockito.verify(user).setLogoutDate(Mockito.any());
        Mockito.verify(userDao).saveUserChanges(user);
    }

    @Test
    public void shouldUpdateUserPassword() throws BadRequestException {
        User user = Mockito.mock();

        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded");

        userService.updateUserPassword(user, "newStrongPassword");

        Mockito.verify(user).setPasswordChangeDate(Mockito.any());

        Mockito.verify(userDao).saveUserChanges(user);
    }
}
