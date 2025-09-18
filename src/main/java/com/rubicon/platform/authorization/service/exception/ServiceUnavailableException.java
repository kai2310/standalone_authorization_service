package com.rubicon.platform.authorization.service.exception;

public class ServiceUnavailableException extends ServiceException
{
    public ServiceUnavailableException()
    {
        this("Service currently unavailable.");
    }

    public ServiceUnavailableException(String message)
    {
        super(503, message);
    }
}
