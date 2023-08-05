package com.brainy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.ResponseWithoutData;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.model.request.UpdateUserRequest;
import com.brainy.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Response<User> getUserInformation(@RequestAttribute User user) {
        return new Response<User>(user, ResponseStatus.SUCCESS);
    }

    @PostMapping
    public ResponseWithoutData updateUser(
        @RequestAttribute User user,
        @RequestBody @Valid UpdateUserRequest request
    ) throws BadRequestException {

        request.applyUpdatesOnUser(user);
        userService.saveUserChanges(user);
        return new ResponseWithoutData(ResponseStatus.SUCCESS);
    }
}
