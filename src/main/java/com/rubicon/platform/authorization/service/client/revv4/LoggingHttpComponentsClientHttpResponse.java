package com.rubicon.platform.authorization.service.client.revv4;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoggingHttpComponentsClientHttpResponse extends AbstractClientHttpResponse
{
    private static Logger logger = LoggerFactory.getLogger(LoggingHttpComponentsClientHttpResponse.class);
    private final HttpResponse httpResponse;
    private HttpHeaders headers;

    public LoggingHttpComponentsClientHttpResponse(HttpResponse httpResponse)
    {
        this.httpResponse = httpResponse;
    }

    public int getRawStatusCode() throws IOException
    {
        return this.httpResponse.getStatusLine().getStatusCode();
    }

    public String getStatusText() throws IOException
    {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }

    public HttpHeaders getHeaders()
    {
        if (this.headers == null)
        {
            this.headers = new HttpHeaders();
            for (Header header : this.httpResponse.getAllHeaders())
            {
                this.headers.add(header.getName(), header.getValue());
            }
        }
        return this.headers;
    }

    public InputStream getBody() throws IOException
    {
        HttpEntity entity = this.httpResponse.getEntity();
        if(entity == null) return null;

        if(logger.isDebugEnabled())
        {
            byte[] bytes = FileCopyUtils.copyToByteArray(entity.getContent());
            logger.debug("Response body: {}",new String(bytes));
            return new ByteArrayInputStream(bytes);

        }
        return entity.getContent();
    }

    public void close()
    {
        HttpEntity entity = this.httpResponse.getEntity();
        if (entity != null)
        {
            try
            {
                // Release underlying connection back to the connection manager
                EntityUtils.consume(entity);
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }

}
