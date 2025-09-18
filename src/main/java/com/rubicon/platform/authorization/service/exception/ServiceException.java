package com.rubicon.platform.authorization.service.exception;

public class ServiceException extends RuntimeException {
    private int responseCode;

    public ServiceException(String message) {
        this(500, message);
    }

    public ServiceException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public ServiceException(int responseCode, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }
}
