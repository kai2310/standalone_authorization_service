package com.rubicon.platform.authorization.service.client.revv4;

public class AuthenticationException extends OAuthAuthenticationClientException
{
    public AuthenticationException(String message)
    {
        super(message,401);
    }
}
