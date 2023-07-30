package com.brainy.util;

import java.util.Map;

public interface Validator {

    boolean isEmail(String email);

    boolean isAllKeysInMap(Map<String, ?> map, String... keys);

    /**
     * Validates if a password is strong enough to be used as a password. 
     */
    boolean isPasswordStrongEnough(String password);

}