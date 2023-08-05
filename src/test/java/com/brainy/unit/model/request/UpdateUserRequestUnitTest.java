package com.brainy.unit.model.request;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        UpdateUserRequest request = new UpdateUserRequest("s", null, "email.com");

        Set<ConstraintViolation<UpdateUserRequest>> violations =
                validator.validate(request);

        Assertions.assertEquals(2, violations.size());
    }
}
