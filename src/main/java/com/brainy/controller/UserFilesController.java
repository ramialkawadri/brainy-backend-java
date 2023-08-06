package com.brainy.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brainy.model.Response;
import com.brainy.model.ResponseWithoutData;
import com.brainy.model.entity.User;
import com.brainy.model.exception.BadRequestException;
import com.brainy.service.UserFilesService;

// TODO: test
@RestController
@RequestMapping("api/files")
public class UserFilesController {

    private UserFilesService userFilesService;
    
    public UserFilesController(UserFilesService userFilesService) {
        this.userFilesService = userFilesService;
    }

    @GetMapping
    public Object getUserFiles(
            @RequestAttribute User user,
            @RequestParam(required = false) String filename) {
        
        Object body = null;

        if (filename != null) 
            body = userFilesService.getFileContent(user.getUsername(), filename);
        else
            body = userFilesService.getUserFiles(user.getUsername());

        return new Response<>(body);
    }

    // Only JSON files can be uploaded through this endpoint
    @PostMapping
    public Response<String> createOrUpdateJsonFile(
            @RequestAttribute User user,
            @RequestParam String filename,
            @RequestBody String body) throws BadRequestException {

        boolean canUserCreateTheFile = userFilesService.canUserCreateFileWithSize(
                user.getUsername(), filename, body.length());

        if (!canUserCreateTheFile) 
            throw new BadRequestException("there isn't enough space");

        userFilesService.createOrUpdateJsonFile(user.getUsername(), filename, body);
        return new Response<>("the file has been created");
    }

    @DeleteMapping
    public ResponseWithoutData deleteFile(
        @RequestAttribute User user,
        @RequestParam String filename
    ) {
        userFilesService.deleteFile(user.getUsername(), filename);
        return new ResponseWithoutData();
    }

    // The response is in bytes
    @GetMapping("size")
    public Response<Long> getUserFileSize(@RequestAttribute User user) {
        long size = userFilesService.getUserFilesSize(user.getUsername());
        return new Response<>(size);
    }
}
