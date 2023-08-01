package com.brainy.service;

import java.time.Instant;
import java.util.Map;

import com.brainy.exception.BadRequestException;
import com.brainy.model.entity.User;

public interface UserService {
    User findUserByUsername(String name);

    void registerUserFromRequest(Map<String, String> request) 
            throws BadRequestException;

    boolean isTokenStillValidForUser(Instant issuedAt, String username);

    void logoutUser(User user);

    void updateUserPassword(User user, String newPassword)
            throws BadRequestException;
}
