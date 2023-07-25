package com.brainy.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatus {
    SUCCESS("success"), ERROR("error");

    private String statusString;

    ResponseStatus(String staString) {
        this.statusString = staString;
    }

    @JsonValue
    public String getStatusString() {
        return statusString;
    }
}
