package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

/**
 * User: mhellkamp
 * Date: 2/12/13
 */
public class LoggingHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory
{
    public LoggingHttpComponentsClientHttpRequestFactory(HttpClient httpClient)
    {
        super(httpClient);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException
    {
        HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        postProcessHttpRequest(httpRequest);
        return new LoggingHttpComponentsClientHttpRequest(getHttpClient(), httpRequest, createHttpContext(httpMethod, uri));
    }
}