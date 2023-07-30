package com.brainy.service;

import java.util.Map;

import com.brainy.exception.BadRequestException;
import com.brainy.model.entity.User;

public interface UserService {
    User findUserByUsername(String name);

    void registerUserFromRequest(Map<String, String> request) 
            throws BadRequestException;
}
