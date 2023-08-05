package com.brainy.model;

public class ResponseWithoutData {
    
    private ResponseStatus status;

    public ResponseWithoutData() {
    }

    public ResponseWithoutData(ResponseStatus status) {
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResponseWithoutData other = (ResponseWithoutData) obj;
        if (status != other.status)
            return false;
        return true;
    }
}
