package com.brainy.integration.model;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

// A wrapper of Response<String>
public class ResponseString extends Response<String> {

    public ResponseString(String body, ResponseStatus status) {
        super(body, status);
    }
}
