package com.brainy.integration.model.wrapper;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;

public class ResponseString extends Response<String> {

    public ResponseString(String body, ResponseStatus status) {
        super(body, status);
    }
}
