package com.rubicon.platform.authorization.service.exception;

public class ForbiddenException extends ServiceException
{
    public ForbiddenException(String message)
    {
        super(403, message);
    }
}
