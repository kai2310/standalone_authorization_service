package com.rubicon.platform.authorization.service.exception;

public class BadRequestException extends ServiceException
{
    public BadRequestException(String message)
    {
        super(400, message);
    }

    public BadRequestException(String message, Throwable cause)
    {
        super(400, message, cause);
    }
}
