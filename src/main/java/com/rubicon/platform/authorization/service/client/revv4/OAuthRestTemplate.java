package com.rubicon.platform.authorization.service.client.revv4;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class OAuthRestTemplate extends RestTemplate
{
    private OAuth2AuthenticationClient authenticationClient;
    private boolean retryBadAccessTokens = true;

    public OAuthRestTemplate(OAuth2AuthenticationClient authenticationClient)
    {
        this.authenticationClient = authenticationClient;
    }

    /**
     * Flag to determine whether a request that has an existing access token, and which then leads to an
     * AccessTokenRequiredException should be retried (immediately, once). Useful if the remote server doesn't recognize
     * an old token which is stored in the client, but is happy to re-grant it.
     *
     * @param retryBadAccessTokens the flag to set (default true)
     */
    public void setRetryBadAccessTokens(boolean retryBadAccessTokens)
    {
        this.retryBadAccessTokens = retryBadAccessTokens;
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException
    {
        RuntimeException rethrow = null;
        try
        {
            return super.doExecute(url, method, requestCallback, responseExtractor);

        }
        catch (HttpClientErrorException e)
        {
            // only catch 401 errors
            if(e.getStatusCode().value() != 401)
                throw e;

            authenticationClient.reset();
            rethrow = e;
        }
        if (retryBadAccessTokens)
        {
            return super.doExecute(url, method, requestCallback, responseExtractor);
        }
        throw rethrow;
    }

    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod method) throws IOException
    {
        uri = appendQueryParameter(uri, authenticationClient.getAccessToken());
        ClientHttpRequest req = super.createRequest(uri, method);
        return req;

    }

    protected URI appendQueryParameter(URI uri, OAuth2AccessToken accessToken)
    {

        try
        {
            // TODO: there is some duplication with UriUtils here. Probably unavoidable as long as this
            // method signature uses URI not String.
            String query = uri.getRawQuery(); // Don't decode anything here
            String token = accessToken.getAccessToken();
            String queryFragment = "access_token=" + URLEncoder.encode(token, "UTF-8");
            if (query == null)
            {
                query = queryFragment;
            }
            else
            {
                query = query + "&" + queryFragment;
            }

            // first form the URI without query and fragment parts, so that it doesn't re-encode some query string chars
            // (SECOAUTH-90)
            URI update = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null,
                    null);
            // now add the encoded query string and the then fragment
            StringBuffer sb = new StringBuffer(update.toString());
            sb.append("?");
            sb.append(query);
            if (uri.getFragment() != null)
            {
                sb.append("#");
                sb.append(uri.getFragment());
            }

            return new URI(sb.toString());

        }
        catch (URISyntaxException e)
        {
            throw new IllegalArgumentException("Could not parse URI", e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalArgumentException("Could not encode URI", e);
        }

    }
}
