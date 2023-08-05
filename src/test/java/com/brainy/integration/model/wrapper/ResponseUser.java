package com.brainy.integration.model.wrapper;

import com.brainy.model.Response;
import com.brainy.model.ResponseStatus;
import com.brainy.model.entity.User;

public class ResponseUser extends Response<User> {

    public ResponseUser(User body, ResponseStatus status) {
        super(body, status);
    }
}
