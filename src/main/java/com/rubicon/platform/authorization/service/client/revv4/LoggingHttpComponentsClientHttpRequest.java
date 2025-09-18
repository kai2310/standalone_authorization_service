package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * User: mhellkamp
 * Date: 2/12/13
 */
public class LoggingHttpComponentsClientHttpRequest extends AbstractClientHttpRequest
{
    private static final Logger logger = LoggerFactory.getLogger(LoggingHttpComponentsClientHttpRequest.class);

    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream();
    private final HttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    public LoggingHttpComponentsClientHttpRequest(HttpClient httpClient,
                                                  HttpUriRequest httpRequest,
                                                  HttpContext httpContext)
    {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }

    public HttpMethod getMethod()
    {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }

    public URI getURI()
    {
        return this.httpRequest.getURI();
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException
    {
        return this.bufferedOutput;
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException
    {
        if(logger.isDebugEnabled())
            logger.debug("Sending {} to {}",httpRequest.getMethod(),httpRequest.getURI());

        if(logger.isDebugEnabled())
            logger.debug("Sending request body: {}",bufferedOutput.toString());

        byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() == -1)
        {
            headers.setContentLength(bytes.length);
        }
        ClientHttpResponse result = executeInternal(headers, bytes);
        this.bufferedOutput = null;
        return result;
    }

    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException
    {
        for (Map.Entry<String, List<String>> entry : headers.entrySet())
        {
            String headerName = entry.getKey();
            if (!headerName.equalsIgnoreCase(HTTP.CONTENT_LEN) &&
                !headerName.equalsIgnoreCase(HTTP.TRANSFER_ENCODING))
            {
                for (String headerValue : entry.getValue())
                {
                    this.httpRequest.addHeader(headerName, headerValue);
                }
            }
        }
        if (this.httpRequest instanceof HttpEntityEnclosingRequest)
        {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) this.httpRequest;
            HttpEntity requestEntity = new ByteArrayEntity(bufferedOutput);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new LoggingHttpComponentsClientHttpResponse(httpResponse);
    }
}
