package com.brainy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

@RestController
@RequestMapping("api")
public class UserInformationController {
    
    @GetMapping("user")
    public Response<String> getUserInformation() {
        return new Response<String>("rami", ResponseStatus.SUCCESS);
    }
}
