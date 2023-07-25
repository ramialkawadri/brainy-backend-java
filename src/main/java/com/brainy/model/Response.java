package com.brainy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response<T> {
    private T data;
    private ResponseStatus status;

    public Response(T body, ResponseStatus status) {
        this.data = body;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T body) {
        this.data = body;
    }

    @JsonProperty("status")
    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
