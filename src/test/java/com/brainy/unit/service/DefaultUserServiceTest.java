package com.brainy.unit.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.brainy.dao.UserDao;
import com.brainy.exception.BadRequestException;
import com.brainy.model.entity.User;
import com.brainy.service.DefaultUserService;
import com.brainy.util.Validator;

public class DefaultUserServiceTest {
    
    private UserDao userDao;
    private DefaultUserService userService;
    private PasswordEncoder passwordEncoder;
    private Validator validator;

    public DefaultUserServiceTest() {
        userDao = Mockito.mock();
        passwordEncoder = Mockito.mock();
        validator = Mockito.mock();
        userService = new DefaultUserService(userDao, passwordEncoder, validator);
    }

    @Test
    public void shouldGetUserByUsername() {
        User mockUser = new User();

        Mockito.when(userDao.findUserByUserName("user")).thenReturn(mockUser);

        User user = userService.findUserByUsername("user");

        Mockito.verify(userDao).findUserByUserName("user");

        Assertions.assertEquals(mockUser, user);
    }

    @Test
    public void shouldRegisterUser() throws BadRequestException {
        Map<String, String> request = createMockRequest();

        setValidatorReturnOnValueOnIsAllKeysInMap(request, true);

        Mockito.when(validator.isEmail(Mockito.any())).thenReturn(true);

        Mockito.when(validator.isPasswordStrongEnough(Mockito.anyString()))
                .thenReturn(true);

        Mockito.doAnswer(invocation -> {
            User user = (User) invocation.getArguments()[0];

            Assertions.assertNotNull(user);
            Assertions.assertNotEquals("testPass", user.getPassword());

            return true;
        }).when(userDao).registerUser(Mockito.any());

        userService.registerUserFromRequest(request);
    }

    @Test
    public void shouldNotRegisterUserBecauseOfMissingProperties() {
        Map<String, String> request = createMockRequest();

        setValidatorReturnOnValueOnIsAllKeysInMap(request, false);

        Mockito.when(validator.isEmail(Mockito.any())).thenReturn(true);

        Mockito.when(validator.isPasswordStrongEnough(Mockito.anyString()))
                .thenReturn(true);

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            userService.registerUserFromRequest(request);
        });
    }

    @Test
    public void shouldNotRegisterUserBecauseOfInvalidEmail() {
        Map<String, String> request = createMockRequest();

        setValidatorReturnOnValueOnIsAllKeysInMap(request, true);

        Mockito.when(validator.isEmail(Mockito.any())).thenReturn(false);

        Mockito.when(validator.isPasswordStrongEnough(Mockito.anyString()))
                .thenReturn(true);

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            userService.registerUserFromRequest(request);
        });
    }

    @Test
    public void shouldNotRegisterUserBecauseOfBadPassword() {
        Map<String, String> request = createMockRequest();

        setValidatorReturnOnValueOnIsAllKeysInMap(request, true);

        Mockito.when(validator.isEmail(Mockito.any())).thenReturn(true);

        Mockito.when(validator.isPasswordStrongEnough(Mockito.anyString()))
                .thenReturn(false);

        Assertions.assertThrowsExactly(BadRequestException.class, () -> {
            userService.registerUserFromRequest(request);
        });
    }

    private Map<String, String> createMockRequest() {
        Map<String, String> request = new HashMap<>();
        request.put("username", "test");
        request.put("password", "testPass");
        request.put("email", "test@test.com");
        request.put("firstName", "test");
        request.put("lastName", "test");
        return request;
    }

    private void setValidatorReturnOnValueOnIsAllKeysInMap(
            Map<String, String> map, boolean val) {
        Mockito.when(validator.isAllKeysInMap(
                map,
                "username",
                "password",
                "email",
                "firstName",
                "lastName"
        )).thenReturn(val);
    }

}
