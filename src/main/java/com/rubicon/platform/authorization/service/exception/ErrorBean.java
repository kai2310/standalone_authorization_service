package com.rubicon.platform.authorization.service.exception;

public class ErrorBean {
    private boolean error = true;
    private String message;
    private int statusCode;

    public ErrorBean() {
    }

    public ErrorBean(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public boolean getError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
