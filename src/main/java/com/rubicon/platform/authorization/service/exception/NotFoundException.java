package com.rubicon.platform.authorization.service.exception;

public class NotFoundException extends ServiceException
{
    public NotFoundException(String message)
    {
        super(404, message);
    }
}
