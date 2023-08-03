package com.brainy.model.exception;

import com.brainy.model.ResponseStatus;

public class BadRequestException extends RequestException {

    public BadRequestException(String message) {
        super(message, ResponseStatus.BAD_REQUEST);
    }
}
