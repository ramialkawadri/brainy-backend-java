package com.brainy.service;

import java.time.Instant;

import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UserRegistrationRequest;

public interface UserService {

    User findUserByUsername(String name);

    void registerUserFromRequest(UserRegistrationRequest request)
            throws BadRequestException;

    boolean isTokenStillValidForUser(Instant issuedAt, String username);

    void logoutUser(User user);

    void updateUserPassword(User user, String newPassword);

    void saveUserChanges(User user) throws BadRequestException;
}
