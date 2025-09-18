package com.rubicon.platform.authorization.hyperion.exception;

import com.dottydingo.hyperion.api.exception.HyperionException;

public class AuthenticationException extends HyperionException
{
    public AuthenticationException(String message)
    {
        super(401, message);
    }

    public AuthenticationException(String message, Throwable throwable)
    {
        super(401, message, throwable);
    }
}
