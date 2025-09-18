package com.rubicon.platform.authorization.service.exception;

public class ValidationException extends ServiceException
{
    public ValidationException(String message)
    {
        super(422, message);
    }
}
