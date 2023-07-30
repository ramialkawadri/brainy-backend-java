package com.brainy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;

@RestController
@RequestMapping("api")
public class UserController {

    @GetMapping("user")
    public Response<User> getUserInformation(@RequestAttribute User user) {
        return new Response<User>(user, ResponseStatus.SUCCESS);
    }
}
