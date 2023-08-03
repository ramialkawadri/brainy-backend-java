package com.brainy.service;

import java.time.Instant;

import com.brainy.model.dto.UserRegistrationDto;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;

public interface UserService {
    User findUserByUsername(String name);

    void registerUserFromRequest(UserRegistrationDto userRegistrationDto) 
            throws BadRequestException;

    boolean isTokenStillValidForUser(Instant issuedAt, String username);

    void logoutUser(User user);

    void updateUserPassword(User user, String newPassword);
}
