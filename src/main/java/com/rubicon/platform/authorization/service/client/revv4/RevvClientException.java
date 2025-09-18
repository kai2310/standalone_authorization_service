package com.rubicon.platform.authorization.service.client.revv4;

public class RevvClientException extends RuntimeException
{
    protected RevvClientException()
    {
    }

    public RevvClientException(String message)
    {
        super(message);
    }

    public RevvClientException(String message, Throwable cause)
    {
        super(message, cause);
    }
}

