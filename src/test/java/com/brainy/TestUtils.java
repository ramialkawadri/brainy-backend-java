package com.brainy;

import java.util.UUID;

import com.brainy.model.entity.User;

public class TestUtils {

    public static User generateRandomUser() {
        String username = UUID.randomUUID().toString();
    
        return new User(
                username,
                "testPassword123",
                username + "@test.com",
                "firstName_" + username,
                "lastName_" + username);
    }
    
}
