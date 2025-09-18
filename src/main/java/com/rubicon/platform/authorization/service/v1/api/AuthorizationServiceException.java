package com.rubicon.platform.authorization.service.v1.api;

import com.rubicon.platform.authorization.service.exception.ServiceException;

/**
 */
public class AuthorizationServiceException extends ServiceException
{
    public AuthorizationServiceException(int responseCode, String message)
    {
        super(responseCode, message);
    }
}
