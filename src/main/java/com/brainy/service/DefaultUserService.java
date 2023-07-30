package com.brainy.service;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.brainy.dao.UserDao;
import com.brainy.exception.BadRequestException;
import com.brainy.model.entity.User;
import com.brainy.util.Validator;

import jakarta.persistence.EntityExistsException;

@Service
public class DefaultUserService implements UserService {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;
    private Validator validator;

    public DefaultUserService(
            UserDao userDao,
            PasswordEncoder passwordEncoder,
            Validator validator) {

        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUserName(username);
    }

    @Override
    public void registerUserFromRequest(Map<String, String> request)
            throws BadRequestException {
        
        checkIfUserRegistrationRequestIsValid(request);

        String encodedPassword = passwordEncoder.encode(request.get("password"));

        User user = new User(
            request.get("username"),
            encodedPassword,
            request.get("email"),
            request.get("firstName"),
            request.get("lastName")
        );

        try {
            userDao.registerUser(user);
        } catch (EntityExistsException | DataIntegrityViolationException e) {
            throw new BadRequestException(
                    "a user with the same username or email already exists"
            );
        }
    }

    private void checkIfUserRegistrationRequestIsValid(Map<String, String> request)
            throws BadRequestException {

        boolean isAllKeysInBody = validator.isAllKeysInMap(
            request, 
            "username",
            "password",
            "email",
            "firstName",
            "lastName");
        
        if (!isAllKeysInBody)
            throw new BadRequestException("please fill all required properties");

       boolean isRequestEmailValid = validator.isEmail(request.get("email"));

       if (!isRequestEmailValid)
            throw new BadRequestException("please provide a valid email");

        boolean isPasswordStrongEnough = validator.isPasswordStrongEnough(
                request.get("password")
        );

        if (!isPasswordStrongEnough)
            throw new BadRequestException(
                    "your password must contain a lowercase and an uppercase" +
                    " letter, and it must be at least 8 characters in length"
            );
    }
}
