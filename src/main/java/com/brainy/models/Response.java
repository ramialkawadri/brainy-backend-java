package com.brainy.models;

public class Response<T> {
    private ResponseStatus status;
    private T data;

    public Response(T body, ResponseStatus status) {
        this.data = body;
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T body) {
        this.data = body;
    }
}
