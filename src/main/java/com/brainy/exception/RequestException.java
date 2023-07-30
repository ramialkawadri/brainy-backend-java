package com.brainy.exception;

import com.brainy.model.ResponseStatus;

public class RequestException extends Exception {
    private final ResponseStatus responseStatus;

    public RequestException(String message, ResponseStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getRequestExceptionTitle() {
        return responseStatus.getStatusString();
    }
}
