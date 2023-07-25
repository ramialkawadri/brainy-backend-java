package com.brainy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.models.Response;
import com.brainy.models.ResponseStatus;

@RestController
public class IndexController {

    @GetMapping("/")
    public Response<String> index() {
        return new Response<String>("Server is Running", ResponseStatus.SUCCESS);
    }
}
