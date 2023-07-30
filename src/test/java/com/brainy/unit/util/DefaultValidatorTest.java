package com.brainy.unit.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.brainy.util.DefaultValidator;
import com.brainy.util.Validator;

public class DefaultValidatorTest {

    private Validator validator;

    public DefaultValidatorTest() {
        validator = new DefaultValidator();
    }

    @Test
    public void shouldAcceptStringsAsEmails() {
        String[] emails = {
            "test@test.com",
            "firstname.initial@domain.com",
            "some-name_213@gmail.com"
        };

        for (String email : emails) {
            Assertions.assertTrue(validator.isEmail(email));
        }
    }

    @Test
    public void shouldNotAcceptStringsAsEmails() {
        String[] invalidEmails = {
            "name",
            "name.domain.com",
            "@gmail.com",
            "!name@google.com!"
        };

        for (String email : invalidEmails) {
            System.out.println(email);
            Assertions.assertFalse(validator.isEmail(email));
        }
    }

    @Test
    public void mapShouldContainAllKeys() {
        Map<String, String> map = new HashMap<>();

        map.put("name", "test");
        map.put("password", "password");
        map.put("email", "mail.google.com");
        map.put("extra key", "test");

        Assertions.assertTrue(
            validator.isAllKeysInMap(map, "name", "password", "email")
        );
    }

    @Test
    public void mapShouldNotContainAllKeys() {
        Map<String, String> map = new HashMap<>();

        map.put("name", "test");
        map.put("password", "password");
        map.put("email", "mail.google.com");
        map.put("extra key", "test");

        Assertions.assertFalse(
            validator.isAllKeysInMap(map, "name", "password", "phone")
        );
    }

    @Test
    public void shouldAcceptPasswordsAsStrongEnough() {
        String[] passwords = {
            "Test1234",
            "StrongPassword"
        };

        for (String password : passwords) {
            Assertions.assertTrue(validator.isPasswordStrongEnough(password));
        }
    }

    @Test
    public void shouldNotAcceptPasswordsAsStrongEnough() {
        String[] passwords = {
            "test1234",
            "INVALID_PASSWORD",
            "short"
        };

        for (String password : passwords) {
            Assertions.assertFalse(validator.isPasswordStrongEnough(password));
        }
    }

}
