package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class OAuth2AuthenticationClient extends AbstractClient
{
    private URI tokenUrl;
    private String clientId;
    private String clientSecret;
    private OAuth2AccessToken accessToken;

    public OAuth2AuthenticationClient(String tokenUrl, String clientId, String clientSecret)
    {
        this(tokenUrl, clientId, clientSecret,10,10);
    }

    public OAuth2AuthenticationClient(String tokenUrl, String clientId, String clientSecret, int socketTimeoutSeconds,
                                      int connectionTimeoutSeconds)
    {
        super(socketTimeoutSeconds, connectionTimeoutSeconds);
        this.tokenUrl = URI.create(tokenUrl);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }


    public synchronized OAuth2AccessToken getAccessToken()
    {
        if(accessToken == null || accessToken.isExpired())
            refreshToken();

        return accessToken;
    }

    public synchronized void reset()
    {
        accessToken = null;
    }

    private void refreshToken()
    {
        try
        {
            HttpPost request = createRequest();
            HttpResponse response = executeRequest(request);

            int value = response.getStatusLine().getStatusCode();
            if(value >= 400)
            {
                OAuthError error = null;
                try
                {
                    error = readResponse(response,OAuthError.class);
                }
                catch (Exception ignore){}
                String msg = null;
                if(error != null)
                {
                    if(error.getErrorDescription() != null)
                        msg = error.getErrorDescription();
                    else
                        msg = error.getError();
                }

                if(msg == null)
                    msg = "An unknown error occurred.";
                if(value == 401)
                    throw new AuthenticationException(msg);
                else if(value == 403)
                    throw new AuthorizationException(msg);
                else
                    throw new OAuthAuthenticationClientException(msg,value);
            }

            this.accessToken = readResponse(response,OAuth2AccessToken.class);
            if(accessToken == null)
                throw new OAuthAuthenticationClientException("No token returned from service.");
        }
        catch(IOException e)
        {
            throw new OAuthAuthenticationClientException("An error occurred retrieving a token.",e);
        }

    }

    protected HttpPost createRequest() throws IOException
    {
        HttpPost request = new HttpPost(tokenUrl);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id",clientId));
        params.add(new BasicNameValuePair("client_secret",clientSecret));
        params.add(new BasicNameValuePair("grant_type","self"));
        request.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));

        return request;
    }

}
