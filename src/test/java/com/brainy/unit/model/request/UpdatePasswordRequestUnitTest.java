package com.brainy.unit.model.request;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.model.request.UpdatePasswordRequest;
import com.brainy.unit.UnitTestUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public class UpdatePasswordRequestUnitTest {
    private Validator validator;

    public UpdatePasswordRequestUnitTest() {
        validator = UnitTestUtils.getValidator();
    }

    @Test
    public void shouldNotUpdatePassword() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("week");

        Set<ConstraintViolation<UpdatePasswordRequest>> violations =
                validator.validate(request);

        Assertions.assertEquals(2, violations.size());
    }
}
