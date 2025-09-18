package com.rubicon.platform.authorization.service.client.revv4;

public class OAuthAuthenticationClientException extends RuntimeException
{
    private int responseCode = 500;

    public OAuthAuthenticationClientException(String message, int responseCode)
    {
        super(message);
        this.responseCode = responseCode;
    }

    public OAuthAuthenticationClientException(String message)
    {
        this(message, 500);
    }

    public OAuthAuthenticationClientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public int getResponseCode()
    {
        return responseCode;
    }
}
