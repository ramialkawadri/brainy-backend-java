package com.brainy.model;

public class ResponseWithoutData {
    
    private ResponseStatus status;

    /**
     * If no ResponseStatus is provided, SUCCESS is used.
     */
    public ResponseWithoutData() {
        this(ResponseStatus.SUCCESS);
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
