package com.brainy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

@RestController
public class IndexController {

    @GetMapping("/")
    public Response<String> getServerStatus() {
        return new Response<String>("server is up and running!",
                ResponseStatus.SUCCESS);
    }
}
