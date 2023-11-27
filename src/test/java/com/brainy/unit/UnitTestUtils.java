package com.brainy.unit;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class UnitTestUtils {

    public static Validator getValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
