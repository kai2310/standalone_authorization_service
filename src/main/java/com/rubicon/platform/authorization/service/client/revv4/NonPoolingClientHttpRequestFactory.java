package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

public class NonPoolingClientHttpRequestFactory implements ClientHttpRequestFactory
{
    SchemeRegistry schemeRegistry;
    private int readTimeout = 0;
    private int connectionTimeout = 0;

    public NonPoolingClientHttpRequestFactory(SchemeRegistry schemeRegistry)
    {
        this.schemeRegistry = schemeRegistry;
    }

    public void setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException
    {
        HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        return new LoggingHttpComponentsClientHttpRequest(getHttpClient(), httpRequest, null);
    }


    /**
     * Create a Commons HttpMethodBase object for the given HTTP method and URI specification.
     * @param httpMethod the HTTP method
     * @param uri the URI
     * @return the Commons HttpMethodBase object
     */
    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        switch (httpMethod) {
            case GET:
                return new HttpGet(uri);
            case DELETE:
                return new HttpDelete(uri);
            case HEAD:
                return new HttpHead(uri);
            case OPTIONS:
                return new HttpOptions(uri);
            case POST:
                return new HttpPost(uri);
            case PUT:
                return new HttpPut(uri);
            case TRACE:
                return new HttpTrace(uri);
            case PATCH:
                return new HttpPatch(uri);
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    public HttpClient getHttpClient()
    {
        BasicClientConnectionManager connectionManager = new BasicClientConnectionManager(schemeRegistry);
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
        httpClient.setRedirectStrategy(new LaxRedirectStrategy());
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
        return httpClient;
    }
}
