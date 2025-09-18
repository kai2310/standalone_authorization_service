package com.rubicon.platform.authorization.service.client.revv4;

public class AuthorizationException extends OAuthAuthenticationClientException
{
    public AuthorizationException(String message)
    {
        super(message,403);
    }
}
