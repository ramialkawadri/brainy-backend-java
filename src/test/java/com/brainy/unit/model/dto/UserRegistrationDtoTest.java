package com.brainy.unit.model.dto;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.model.dto.UserRegistrationDto;
import com.brainy.unit.UnitTestUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class UserRegistrationDtoTest {

    private Validator validator;

    public UserRegistrationDtoTest() {
        this.validator = UnitTestUtils.getValidator();
    }

    @Test
    public void shouldAcceptValidValues() {
        UserRegistrationDto user = new UserRegistrationDto(
                "username",
                "StrongPassword1",
                "test@test.com",
                "firstName",
                "lastName");

        Set<ConstraintViolation<UserRegistrationDto>> violations =
                validator.validate(user);

        Assertions.assertEquals(0, violations.size());
    }
    
    @Test
    public void shouldNotAcceptBlanks() {
        UserRegistrationDto user = new UserRegistrationDto("", "", "", "", "");

        Set<ConstraintViolation<UserRegistrationDto>> violations =
                validator.validate(user);

        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    public void shouldNotAcceptInvalidEmail() {
        UserRegistrationDto user = new UserRegistrationDto(
                "username",
                "StrongPassword1",
                "test@.com",
                "firstName",
                "lastName");

        Set<ConstraintViolation<UserRegistrationDto>> violations =
                validator.validate(user);

        Assertions.assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotAcceptShortPassword() {
        UserRegistrationDto user = new UserRegistrationDto(
                "username",
                "Pass1",
                "test@test.com",
                "firstName",
                "lastName");

        Set<ConstraintViolation<UserRegistrationDto>> violations =
                validator.validate(user);

        // 2 Because of the regular expression also counts
        Assertions.assertEquals(2, violations.size());
    }

    @Test
    public void shouldNotAcceptWeekPassword() {
        UserRegistrationDto user = new UserRegistrationDto(
                "username",
                "password",
                "test@test.com",
                "firstName",
                "lastName");

        Set<ConstraintViolation<UserRegistrationDto>> violations =
                validator.validate(user);

        Assertions.assertEquals(1, violations.size());
    }
}
