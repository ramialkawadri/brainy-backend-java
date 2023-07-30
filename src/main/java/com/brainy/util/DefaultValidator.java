package com.brainy.util;

import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class DefaultValidator implements Validator {

    @Override
    public boolean isEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();

        return validator.isValid(email);
    }

    @Override
    public boolean isAllKeysInMap(Map<String, ?> map, String ...keys) {
        for (String key : keys) {
            if (map.get(key) == null)
                return false;
        }

        return true;
    }

    @Override
    public boolean isPasswordStrongEnough(String password) {
        boolean isPasswordLongEnough = password.length() >= 8;
        boolean containsLowercaseLetter = containsLowercaseLetter(password);
        boolean containsUppercaseLetter = containsUppercaseLetter(password);

        return isPasswordLongEnough && containsLowercaseLetter &&
                containsUppercaseLetter;
    }

    private boolean containsLowercaseLetter(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if ('a' <= c && c <= 'z')
                return true;
        }

        return false;
    }

    private boolean containsUppercaseLetter(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if ('A' <= c && c <= 'Z')
                return true;
        }

        return false;
    }
}
