package com.brainy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.brainy.exception.RequestException;
import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    

@ExceptionHandler(RequestException.class)
    public ResponseEntity<Response<String>>
                    requestExceptionHandler(RequestException e) {

        String responseString = e.getRequestExceptionTitle() + ": " + e.getMessage();

        Response<String> responseBody = new Response<String>
        (responseString, e.getResponseStatus());

        return new ResponseEntity<Response<String>>
                (responseBody, HttpStatus.BAD_REQUEST);
    }

    // Must be the last exception handler!
    @ExceptionHandler
    public ResponseEntity<Response<String>> exceptionHandler(Exception e) {
        e.printStackTrace();

        Response<String> responseBody = new Response<String>
        ("error: an error has occurred", ResponseStatus.ERROR);

        return new ResponseEntity<Response<String>>
                (responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
