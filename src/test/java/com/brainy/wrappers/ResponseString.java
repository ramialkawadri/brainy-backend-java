package com.brainy.wrappers;

import com.brainy.models.Response;
import com.brainy.models.ResponseStatus;

public class ResponseString extends Response<String> {

    public ResponseString(String body, ResponseStatus status) {
        super(body, status);
    }
}
